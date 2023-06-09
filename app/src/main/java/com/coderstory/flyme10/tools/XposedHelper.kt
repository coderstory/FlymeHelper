package com.coderstory.flyme10.tools

import android.content.Context
import android.content.pm.PackageManager
import com.alibaba.fastjson.JSONObject
import com.coderstory.flyme10.BuildConfig
import com.google.gson.Gson
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException
import java.util.zip.ZipFile

open class XposedHelper {
    var prefs: XSharedPreferences
    fun hookAllConstructors(p1: String, parameterTypesAndCallback: XC_MethodHook) {
        try {
            val packageParser = findClass(p1, null)
            hookAllConstructors(packageParser, parameterTypesAndCallback)
        } catch (e: Throwable) {
            if (BuildConfig.DEBUG) XposedBridge.log(e)
        }
    }

    protected fun hookAllConstructors(
        hookClass: Class<*>,
        callback: XC_MethodHook
    ): Set<XC_MethodHook.Unhook> {
        return try {
            val result = XposedBridge.hookAllConstructors(hookClass, callback)
            if (result.size == 0) {
                XposedBridge.log("类" + hookClass.name + "中的构造方法没有被hook到")
            }
            result
        } catch (error: Throwable) {
            XposedBridge.log(error)
            HashSet()
        }
    }

    protected fun findClassWithoutLog(classpatch: String, classLoader: ClassLoader): Class<*>? {
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
     * @param packageName the module package name
     * @return the file
     */
    private fun findApkFile(context: Context, packageName: String): File? {
        return try {
            val moduleContext = context.createPackageContext(
                packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
            val apkPath = moduleContext.packageCodePath
            File(apkPath)
        } catch (e: PackageManager.NameNotFoundException) {
            Logger.loge(String.format("Find File Error，Package:%s", packageName))
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

    @Throws(
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
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
            Logger.loge(
                String.format(
                    "Set NeedHookPackage Accounding:%s Error",
                    BuildConfig.APPLICATION_ID
                )
            )
        }
    }

    companion object {
        var json = JSONObject()
        fun findClass(classpatch: String, classLoader: ClassLoader?): Class<*> {
            try {
                return XposedHelpers.findClass(classpatch, classLoader)
            } catch (error: ClassNotFoundError) {
                XposedBridge.log(error)
            }
            return String::class.java
        }

        fun findAndHookMethod(
            p1: String,
            param: ClassLoader,
            p2: String,
            vararg parameterTypesAndCallback: Any?
        ) {
            try {
                XposedHelpers.findAndHookMethod(p1, param, p2, *parameterTypesAndCallback)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        fun findAndHookMethod(p1: Class<*>, p2: String, vararg parameterTypesAndCallback: Any) {
            try {
                XposedHelpers.findAndHookMethod(p1, p2, *parameterTypesAndCallback)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        fun hookAllConstructors(
            p1: String,
            classLoader: ClassLoader,
            parameterTypesAndCallback: XC_MethodHook
        ) {
            try {
                val packageParser = XposedHelpers.findClass(p1, classLoader)
                XposedBridge.hookAllConstructors(packageParser, parameterTypesAndCallback)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        protected fun findAndHookMethod(p1: String, p2: String, p3: Array<Any>) {
            try {
                XposedHelpers.findAndHookMethod(Class.forName(p1), p2, *p3)
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
        }

        fun hookAllMethods(
            p1: String,
            classLoader: ClassLoader?,
            methodName: String,
            parameterTypesAndCallback: XC_MethodHook
        ): Int {
            return try {
                val packageParser = XposedHelpers.findClass(p1, classLoader)
                val count = XposedBridge.hookAllMethods(
                    packageParser,
                    methodName,
                    parameterTypesAndCallback
                ).size
                if (count == 0) {
                    XposedBridge.log("类" + p1 + "中的方法" + methodName + "没有被hook到")
                }
                count
            } catch (error: Throwable) {
                XposedBridge.log(error)
                0
            }
        }

        fun hookAllMethods(
            hookClass: Class<*>,
            methodName: String,
            callback: XC_MethodHook
        ): Set<XC_MethodHook.Unhook>? {
            try {
                val result = XposedBridge.hookAllMethods(hookClass, methodName, callback)
                if (result.size == 0) {
                    XposedBridge.log("类" + hookClass.name + "中的方法" + methodName + "没有被hook到")
                }
                return result
            } catch (error: Throwable) {
                XposedBridge.log(error)
            }
            return null
        }
    }

    init {
        prefs = XSharedPreferences(BuildConfig.APPLICATION_ID, Misc.SharedPreferencesName)
        if (prefs.all.keys.size == 0) {
            prefs =
                XSharedPreferences(File("/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml"))
        }
    }
}