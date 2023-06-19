package com.coderstory.flyme10.patchModule


import android.content.*
import android.net.Uri
import com.coderstory.flyme10.tools.XposedHelper
import com.coderstory.flyme10.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method


class ThemePatcher : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {
        if (respray.packageName == "com.meizu.customizecenter" && prefs.getBoolean(
                "enabletheme",
                false
            )
        ) {
            if (respray.packageName == "com.meizu.customizecenter") {
                respray.res.setReplacement(0x7f110338, "开始白嫖")
                respray.res.setReplacement(0x7f11033f, "开始白嫖")
                respray.res.setReplacement(0x7f110345, "正在白嫖")
            }
        }
    }
    override fun handleLoadPackage(param: LoadPackageParam) {

        // 主题和谐
        if (param.packageName == "com.meizu.customizecenter" && prefs.getBoolean(
                "enabletheme",
                false
            )
        ) {
            val apkPath = param.appInfo.sourceDir
            System.loadLibrary("dexkit")

            if (param.packageName == "com.meizu.customizecenter") {
                XposedBridge.log("开始hook主题")
                DexKitBridge.create(apkPath)?.use { bridge ->
                    var searchResults = bridge.batchFindMethodsUsingStrings {
                        addQuery("resetToSystemTheme", setOf("resetToSystemTheme"))
                        addQuery(
                            "trialEnd",
                            setOf(
                                "com.meizu.customizecenter.theme.trial.end",
                                "com.meizu.customizecenter"
                            )
                        )
                        addQuery(
                            "startFontRestoreService",
                            setOf(
                                "is_go_to_pay_font",
                                "is_restore_last_key",
                                "startFontRestoreService"
                            )
                        )

                        addQuery(
                            "checkTime",
                            setOf(
                                "checkTrialFontWhenAppStart_interval:",
                                " second"
                            )
                        )

                        addQuery(
                            "checkTrialFont",
                            setOf("checkTrialFont:!isUsingTrialFont()", "checkTrialFont_interval:")
                        )
                    }

                    searchResults["resetToSystemTheme"]?.let { it ->
                        it.forEach {
                            val method: Method = it.getMethodInstance(param.classLoader)
                            XposedBridge.hookMethod(
                                method,
                                XC_MethodReplacement.returnConstant(true)
                            )
                        }
                    }

                    searchResults["trialEnd"]?.let { it ->
                        it.forEach {
                            val method: Method = it.getMethodInstance(param.classLoader)
                            XposedBridge.hookMethod(
                                method,
                                XC_MethodReplacement.returnConstant(true)
                            )
                        }
                    }

                    searchResults["startFontRestoreService"]?.let { it ->
                        it.forEach {
                            val method: Method = it.getMethodInstance(param.classLoader)
                            XposedBridge.hookMethod(
                                method,
                                XC_MethodReplacement.returnConstant(null)
                            )
                        }
                    }

                    searchResults["checkTime"]?.let { it ->
                        it.forEach {
                            val method: Method = it.getMethodInstance(param.classLoader)
                            XposedBridge.hookMethod(
                                method,
                                XC_MethodReplacement.returnConstant(null)
                            )
                        }
                    }

                    searchResults["checkTrialFont"]?.let { it ->
                        it.forEach {
                            val method: Method = it.getMethodInstance(param.classLoader)
                            XposedBridge.hookMethod(
                                method,
                                XC_MethodReplacement.returnConstant(null)
                            )
                        }
                    }

                } ?: XposedBridge.log("search result empty")


                //主题混搭 ThemeContentProvider query Unknown URI
                findAndHookMethod(
                    findClass(
                        "com.meizu.customizecenter.manager.utilshelper.dbhelper.dao.ThemeContentProvider",
                        param.classLoader
                    ),
                    "query",
                    Uri::class.java,
                    Array<String>::class.java,
                    String::class.java,
                    Array<String>::class.java,
                    String::class.java,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val objs = param.args
                            val tag = "(ITEMS LIKE"
                            val tag2 = "%zklockscreen;%"
                            val tag3 = "%com.meizu.flyme.weather;%"
                            var result = false
                            for (obj in objs) {
                                if (obj is String && (obj.contains(tag) || obj == tag2 || obj == tag3)) {
                                    result = true
                                }
                            }
                            if (result) {
                                for (obj in objs) {
                                    if (obj is Array<*>) { // obj is String[]
                                        for (j in (obj as Array<String>).indices) {
                                            if (obj[j].contains("/storage/emulated/0/Customize/Themes")) {
                                                obj[j] = "/storage/emulated/0/Customize%"
                                            } else if (obj[j].contains("/storage/emulated/0/Customize/TrialThemes")) {
                                                obj[j] = "NONE"
                                            }
                                        }
                                    }
                                }
                            }
                            super.beforeHookedMethod(param)
                        }
                    })

                // android 11 8.30.2
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.utilshelper.restorehelper.ThemeRestoreService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(0)
                )
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.utilshelper.restorehelper.FontRestoreService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(0)
                )
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.utilshelper.scannerhelper.CustomizeScannerService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(0)
                )

                // 拦截开机自启广播
                findAndHookMethod(
                    "com.meizu.customizecenter.admin.receiver.BootBroadcastReceiver",
                    param.classLoader,
                    "onReceive",
                    Context::class.java,
                    Intent::class.java,
                    XC_MethodReplacement.returnConstant(null)
                )

                // 拦截试用服务
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.font.FontTrialService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(2)
                )
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.theme.ThemeTrialService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    object : XC_MethodReplacement() {
                        @Throws(Throwable::class)
                        override fun replaceHookedMethod(param: MethodHookParam): Any {
                            return 2
                        }
                    })
            }
        }
    }

    override fun initZygote(startupParam: StartupParam?) {
        TODO("Not yet implemented")
    }


}