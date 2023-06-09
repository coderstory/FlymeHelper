package com.coderstory.flyme10.tools

import com.coderstory.flyme10.BuildConfig
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import java.io.File

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
        return try {
            XposedHelpers.findClass(classpatch, classLoader)
        } catch (error: Exception) {
            null
        }
    }


    companion object {
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