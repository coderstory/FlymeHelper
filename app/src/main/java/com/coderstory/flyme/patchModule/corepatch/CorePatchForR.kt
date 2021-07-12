package com.coderstory.flyme.patchModule.corepatch

import android.content.pm.ApplicationInfo
import android.content.pm.Signature
import com.coderstory.flyme.tools.ReturnConstant
import com.coderstory.flyme.tools.XposedHelper
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.InvocationTargetException
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.zip.ZipEntry

class CorePatchForR : XposedHelper(), IXposedHookLoadPackage, IXposedHookZygoteInit {
    // var prefs = XSharedPreferences(BuildConfig.APPLICATION_ID, "conf")

    @Throws(IllegalAccessException::class, InvocationTargetException::class, InstantiationException::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {

        // 允许降级
        XposedHelper.Companion.findAndHookMethod("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader,
                "checkDowngrade",
                "com.android.server.pm.parsing.pkg.AndroidPackage",
                "android.content.pm.PackageInfoLite",
                ReturnConstant(prefs, "downgrade", null))

        // exists on flyme 9(Android 11) only
        XposedHelper.Companion.findAndHookMethod("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader,
                "checkDowngrade",
                "android.content.pm.PackageInfoLite",
                "android.content.pm.PackageInfoLite",
                ReturnConstant(prefs, "downgrade", true))


        // apk内文件修改后 digest校验会失败
        XposedHelper.Companion.hookAllMethods("android.util.jar.StrictJarVerifier", loadPackageParam.classLoader, "verifyMessageDigest",
                ReturnConstant(prefs, "authcreak", true))
        XposedHelper.Companion.hookAllMethods("android.util.jar.StrictJarVerifier", loadPackageParam.classLoader, "verify",
                ReturnConstant(prefs, "authcreak", true))
        XposedHelper.Companion.hookAllMethods("java.security.MessageDigest", loadPackageParam.classLoader, "isEqual",
                ReturnConstant(prefs, "authcreak", true))

        // Targeting R+ (version " + Build.VERSION_CODES.R + " and above) requires"
        // + " the resources.arsc of installed APKs to be stored uncompressed"
        // + " and aligned on a 4-byte boundary
        // target >=30 的情况下 resources.arsc 必须是未压缩的且4K对齐
        XposedHelper.Companion.hookAllMethods("android.content.res.AssetManager", loadPackageParam.classLoader, "containsAllocatedTable",
                ReturnConstant(prefs, "authcreak", false))

        // No signature found in package of version " + minSignatureSchemeVersion
        // + " or newer for package " + apkPath
        XposedHelper.Companion.findAndHookMethod("android.util.apk.ApkSignatureVerifier", loadPackageParam.classLoader, "getMinimumSignatureSchemeVersionForTargetSdk", Int::class.javaPrimitiveType,
                ReturnConstant(prefs, "authcreak", 0))
        XposedHelper.Companion.findAndHookMethod("com.android.apksig.ApkVerifier", loadPackageParam.classLoader, "getMinimumSignatureSchemeVersionForTargetSdk", Int::class.javaPrimitiveType,
                ReturnConstant(prefs, "authcreak", 0))

        // Package " + packageName + " signatures do not match previously installed version; ignoring!"
        // public boolean checkCapability(String sha256String, @CertCapabilities int flags) {
        // public boolean checkCapability(SigningDetails oldDetails, @CertCapabilities int flags)
        XposedHelper.Companion.hookAllMethods("android.content.pm.PackageParser", loadPackageParam.classLoader, "checkCapability", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // Don't handle PERMISSION (grant SIGNATURE permissions to pkgs with this cert)
                // Or applications will have all privileged permissions
                // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/content/pm/PackageParser.java;l=5947?q=CertCapabilities
                if (prefs.getBoolean("authcreak", true)) {
                    if (param.args[1] as Int != 4) {
                        param.result = true
                    }
                }
            }
        })

        // 当verifyV1Signature抛出转换异常时，替换一个签名作为返回值
        // 如果用户已安装apk，并且其定义了私有权限，则安装时会因签名与模块内硬编码的不一致而被拒绝。尝试从待安装apk中获取签名。如果其中apk的签名和已安装的一致（只动了内容）就没有问题。此策略可能有潜在的安全隐患。
        val pkc = XposedHelpers.findClass("sun.security.pkcs.PKCS7", loadPackageParam.classLoader)
        val constructor = XposedHelpers.findConstructorExact(pkc, ByteArray::class.java)
        constructor.isAccessible = true
        val ASV = XposedHelpers.findClass("android.util.apk.ApkSignatureVerifier", loadPackageParam.classLoader)
        val sJarClass = XposedHelpers.findClass("android.util.jar.StrictJarFile", loadPackageParam.classLoader)
        val constructorExact = XposedHelpers.findConstructorExact(sJarClass, String::class.java, Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
        constructorExact.isAccessible = true
        val signingDetails = XposedHelpers.findClass("android.content.pm.PackageParser.SigningDetails", loadPackageParam.classLoader)
        val findConstructorExact = XposedHelpers.findConstructorExact(signingDetails, Array<Signature>::class.java, Integer.TYPE)
        findConstructorExact.isAccessible = true
        val packageParserException = XposedHelpers.findClass("android.content.pm.PackageParser.PackageParserException", loadPackageParam.classLoader)
        val error = XposedHelpers.findField(packageParserException, "error")
        error.isAccessible = true
        val signingDetailsArgs = arrayOfNulls<Any>(2)
        signingDetailsArgs[1] = 1
        XposedHelper.Companion.hookAllMethods("android.util.jar.StrictJarVerifier", loadPackageParam.classLoader, "verifyBytes", object : XC_MethodHook() {
            @Throws(Throwable::class)
            public override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                if (prefs.getBoolean("digestCreak", true)) {
                    val block = constructor.newInstance(param.args[0])
                    val infos = XposedHelpers.callMethod(block, "getSignerInfos") as Array<Any>
                    val info = infos[0]
                    val verifiedSignerCertChain = XposedHelpers.callMethod(info, "getCertificateChain", block) as List<X509Certificate>
                    param.result = verifiedSignerCertChain.toTypedArray()
                }
            }
        })
        XposedHelper.Companion.hookAllMethods("android.util.apk.ApkSignatureVerifier", loadPackageParam.classLoader, "verifyV1Signature", object : XC_MethodHook() {
            @Throws(Throwable::class)
            public override fun afterHookedMethod(methodHookParam: MethodHookParam) {
                super.afterHookedMethod(methodHookParam)
                if (prefs.getBoolean("authcreak", true)) {
                    val throwable = methodHookParam.throwable
                    if (throwable != null) {
                        val origJarFile = constructorExact.newInstance(methodHookParam.args[0], true, false)
                        val manifestEntry = XposedHelpers.callMethod(origJarFile, "findEntry", "AndroidManifest.xml") as ZipEntry
                        val lastCerts = XposedHelpers.callStaticMethod(ASV, "loadCertificates", origJarFile, manifestEntry) as Array<Array<Certificate>>
                        val lastSigs = XposedHelpers.callStaticMethod(ASV, "convertToSignatures", lastCerts as Any) as Array<Signature>
                        if (lastSigs != null) {
                            signingDetailsArgs[0] = lastSigs
                        } else {
                            signingDetailsArgs[0] = arrayOf(Signature(SIGNATURE))
                        }
                        val newInstance = findConstructorExact.newInstance(*signingDetailsArgs)
                        val cause = throwable.cause
                        if (throwable.javaClass == packageParserException) {
                            if (error.getInt(throwable) == -103) {
                                methodHookParam.result = newInstance
                            }
                        }
                        if (cause != null && cause.javaClass == packageParserException) {
                            if (error.getInt(cause) == -103) {
                                methodHookParam.result = newInstance
                            }
                        }
                    }
                }
            }
        })


        //New package has a different signature
        //处理覆盖安装但签名不一致
        XposedHelper.Companion.hookAllMethods(signingDetails, "checkCapability", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // Don't handle PERMISSION (grant SIGNATURE permissions to pkgs with this cert)
                // Or applications will have all privileged permissions
                // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/content/pm/PackageParser.java;l=5947?q=CertCapabilities
                if (param.args[1] as Int != 4 && prefs.getBoolean("digestCreak", true)) {
                    param.result = true
                }
            }
        })
        // if app is system app, allow to use hidden api, even if app not using a system signature
        XposedHelper.Companion.findAndHookMethod("android.content.pm.ApplicationInfo", loadPackageParam.classLoader, "isPackageWhitelistedForHiddenApis", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                if (prefs.getBoolean("digestCreak", true)) {
                    val info = param.thisObject as ApplicationInfo
                    if (info.flags and ApplicationInfo.FLAG_SYSTEM != 0
                            || info.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
                        param.result = true
                    }
                }
            }
        })
    }

    override fun initZygote(startupParam: StartupParam) {
        XposedHelper.Companion.hookAllMethods("android.content.pm.PackageParser", null, "getApkSigningVersion", XC_MethodReplacement.returnConstant(1))
        hookAllConstructors("android.util.jar.StrictJarVerifier", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (prefs.getBoolean("enhancedMode", false)) {
                    super.beforeHookedMethod(param)
                    param.args[3] = java.lang.Boolean.FALSE
                }
            }
        })
    }
}