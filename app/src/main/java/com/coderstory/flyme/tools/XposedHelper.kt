package com.coderstory.flyme.tools

import android.content.Context
import android.content.pm.PackageManager
import com.alibaba.fastjson.JSONObject
import com.coderstory.flyme.BuildConfig
import com.google.gson.Gson
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.zip.ZipFile

open class XposedHelper {
    var SIGNATURE = "308203c6308202aea003020102021426d148b7c65944abcf3a683b4c3dd3b139c4ec85300d06092a864886f70d01010b05003074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f6964301e170d3139303130323138353233385a170d3439303130323138353233385a3074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f696430820122300d06092a864886f70d01010105000382010f003082010a028201010087fcde48d9beaeba37b733a397ae586fb42b6c3f4ce758dc3ef1327754a049b58f738664ece587994f1c6362f98c9be5fe82c72177260c390781f74a10a8a6f05a6b5ca0c7c5826e15526d8d7f0e74f2170064896b0cf32634a388e1a975ed6bab10744d9b371cba85069834bf098f1de0205cdee8e715759d302a64d248067a15b9beea11b61305e367ac71b1a898bf2eec7342109c9c5813a579d8a1b3e6a3fe290ea82e27fdba748a663f73cca5807cff1e4ad6f3ccca7c02945926a47279d1159599d4ecf01c9d0b62e385c6320a7a1e4ddc9833f237e814b34024b9ad108a5b00786ea15593a50ca7987cbbdc203c096eed5ff4bf8a63d27d33ecc963990203010001a350304e300c0603551d13040530030101ff301d0603551d0e04160414a361efb002034d596c3a60ad7b0332012a16aee3301f0603551d23041830168014a361efb002034d596c3a60ad7b0332012a16aee3300d06092a864886f70d01010b0500038201010022ccb684a7a8706f3ee7c81d6750fd662bf39f84805862040b625ddf378eeefae5a4f1f283deea61a3c7f8e7963fd745415153a531912b82b596e7409287ba26fb80cedba18f22ae3d987466e1fdd88e440402b2ea2819db5392cadee501350e81b8791675ea1a2ed7ef7696dff273f13fb742bb9625fa12ce9c2cb0b7b3d94b21792f1252b1d9e4f7012cb341b62ff556e6864b40927e942065d8f0f51273fcda979b8832dd5562c79acf719de6be5aee2a85f89265b071bf38339e2d31041bc501d5e0c034ab1cd9c64353b10ee70b49274093d13f733eb9d3543140814c72f8e003f301c7a00b1872cc008ad55e26df2e8f07441002c4bcb7dc746745f0db"
    protected var prefs: XSharedPreferences? = null
    fun hookAllConstructors(p1: String?, parameterTypesAndCallback: XC_MethodHook?) {
        try {
            val packageParser = findClass(p1, null)
            hookAllConstructors(packageParser, parameterTypesAndCallback)
        } catch (e: Throwable) {
            if (BuildConfig.DEBUG) XposedBridge.log(e)
        }
    }

    protected fun hookAllConstructors(hookClass: Class<*>?, callback: XC_MethodHook?): Set<XC_MethodHook.Unhook> {
        return try {
            val result = XposedBridge.hookAllConstructors(hookClass, callback)
            if (result.size == 0) {
                XposedBridge.log("类" + hookClass!!.name + "中的构造方法没有被hook到")
            }
            result
        } catch (error: Throwable) {
            XposedBridge.log(error)
            HashSet()
        }
    }

    protected fun findClassWithoutLog(classpatch: String?, classLoader: ClassLoader?): Class<*>? {
        try {
            return XposedHelpers.findClass(classpatch, classLoader)
        } catch (error: ClassNotFoundError) {
            //XposedBridge.log(error);
        }
        return null
    }

    /**
     * Find apk file file. 获取需要Hook Apk文件地址
     *
     * @param context           the context
     * @param modulePackageName the module package name
     * @return the file
     */
    private fun findApkFile(context: Context?, modulePackageName: String): File? {
        if (context == null) {
            throw RuntimeException("Can't Get Context")
        }
        return try {
            val moudleContext = context.createPackageContext(modulePackageName, Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            val apkPath = moudleContext.packageCodePath
            File(apkPath)
        } catch (e: PackageManager.NameNotFoundException) {
            Logger.loge(String.format("Find File Error，Package:%s", modulePackageName))
            null
        }
    }

    /**
     * Sets need hook package.
     *
     * @param context the context
     */
    private fun getConfig(context: Context) {
        try {
            val path = findApkFile(context, BuildConfig.APPLICATION_ID).toString()
            val zipFile = ZipFile(path)
            val zipEntry = zipFile.getEntry("assets/config")
            val inputStream = zipFile.getInputStream(zipEntry)
            val `in` = InputStreamReader(inputStream)
            val br = BufferedReader(`in`)
            var line: String?
            val sb = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            json = Gson().fromJson(sb.toString(), JSONObject::class.java)
            br.close()
            `in`.close()
            inputStream.close()
        } catch (e: Exception) {
            Logger.loge(e.toString())
        }
    }

    @Throws(IllegalAccessException::class, InvocationTargetException::class, InstantiationException::class)
    fun initJson(loadPackageParam: LoadPackageParam) {
        try {
            // 获取context对象
            val context = XposedHelpers.callMethod(
                    XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass(
                                    "android.app.ActivityThread",
                                    loadPackageParam.classLoader
                            ),
                            "currentActivityThread"
                    ),
                    "getSystemContext"
            ) as Context
            getConfig(context)
        } catch (e: Exception) {
            Logger.loge(String.format("Set NeedHookPackage Accounding:%s Error", BuildConfig.APPLICATION_ID))
        }
    }

    companion object {
        var json = JSONObject()
        fun findClass(classpatch: String?, classLoader: ClassLoader?): Class<*>? {
            try {
                return XposedHelpers.findClass(classpatch, classLoader)
            } catch (error: ClassNotFoundError) {
                XposedBridge.log(error)
            }
            return null
        }

        fun findAndHookMethod(p1: String?, lpparam: ClassLoader?, p2: String?, vararg parameterTypesAndCallback: Any?) {
            try {
                XposedHelpers.findAndHookMethod(p1, lpparam, p2, *parameterTypesAndCallback)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        fun findAndHookMethod(p1: Class<*>?, p2: String?, vararg parameterTypesAndCallback: Any?) {
            try {
                XposedHelpers.findAndHookMethod(p1, p2, *parameterTypesAndCallback)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        fun hookAllConstructors(p1: String?, classLoader: ClassLoader?, parameterTypesAndCallback: XC_MethodHook?) {
            try {
                val packageParser = XposedHelpers.findClass(p1, classLoader)
                XposedBridge.hookAllConstructors(packageParser, parameterTypesAndCallback)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        protected fun findAndHookMethod(p1: String?, p2: String?, p3: Array<Any?>) {
            try {
                XposedHelpers.findAndHookMethod(Class.forName(p1!!), p2, *p3)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        fun hookAllMethods(p1: String, lpparam: ClassLoader?, methodName: String, parameterTypesAndCallback: XC_MethodHook?): Int {
            return try {
                val packageParser = XposedHelpers.findClass(p1, lpparam)
                val count = XposedBridge.hookAllMethods(packageParser, methodName, parameterTypesAndCallback).size
                if (count == 0) {
                    XposedBridge.log("类" + p1 + "中的方法" + methodName + "没有被hook到")
                }
                count
            } catch (error: Throwable) {
                XposedBridge.log(error)
                0
            }
        }

        fun hookAllMethods(hookClass: Class<*>?, methodName: String, callback: XC_MethodHook?): Set<XC_MethodHook.Unhook>? {
            try {
                val result = XposedBridge.hookAllMethods(hookClass, methodName, callback)
                if (result.size == 0) {
                    XposedBridge.log("类" + hookClass!!.name + "中的方法" + methodName + "没有被hook到")
                }
                return result
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
            return null
        }

        fun writeFile(file: File?, file1: File?) {
            val fileInputStream: FileInputStream
            val fileOutputStream: FileOutputStream
            val bufferedInputStream: BufferedInputStream
            val bufferedOutputStream: BufferedOutputStream
            try {
                fileInputStream = FileInputStream(file)
                fileOutputStream = FileOutputStream(file1)
                if (!file1!!.parentFile.exists()) {
                    file1.parentFile.mkdirs()
                }
                bufferedInputStream = BufferedInputStream(fileInputStream)
                bufferedOutputStream = BufferedOutputStream(fileOutputStream)
                val bytes = ByteArray(5120)
                while (true) {
                    val read = bufferedInputStream.read(bytes)
                    if (read == -1) {
                        break
                    }
                    bufferedOutputStream.write(bytes, 0, read)
                }
                bufferedOutputStream.flush()
                bufferedInputStream.close()
                bufferedOutputStream.close()
                fileOutputStream.close()
                fileInputStream.close()
            } catch (error: IOException) {
                XposedBridge.log(error)
            }
        }
    }

    init {
        prefs = XSharedPreferences(BuildConfig.APPLICATION_ID, Misc.SharedPreferencesName)
        if (prefs!!.all.keys.size == 0) {
            prefs = XSharedPreferences(File("/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml"))
        }
    }
}