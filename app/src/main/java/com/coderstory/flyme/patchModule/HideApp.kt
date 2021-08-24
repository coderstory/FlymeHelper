package com.coderstory.flyme.patchModule


import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.os.Build
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.util.*
import java.util.stream.Collectors

class HideApp : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {}
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (loadPackageParam.packageName == "com.meizu.flyme.launcher") {
            // bl.add(new ComponentName("com.android.vending", "com.android.vending.MarketWidgetProvider"));
            val value = prefs.getString("Hide_App_List", "")
            XposedBridge.log("load config$value")
            if (value != "") {
                val hideAppList = Arrays.asList(*value!!.split(":").toTypedArray())
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    val clazz: Class<*> = findClass("com.meizu.flyme.launcher.co", loadPackageParam.classLoader)
                    findAndHookMethod("com.meizu.flyme.launcher.MzWidgetGroupView", loadPackageParam.classLoader, "a", clazz, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            super.beforeHookedMethod(param)
                            val obj = param.args[0]
                            var list = XposedHelpers.getObjectField(obj, "a") as List<AppWidgetProviderInfo>
                            //XposedBridge.log("个数1" + list.size());
                            list = list.stream().filter { item: Any -> value.contains((item as AppWidgetProviderInfo).provider.packageName) }.collect(Collectors.toList())
                            XposedHelpers.setObjectField(obj, "a", list)
                            //XposedBridge.log("个数2" + list.size());
                        }
                    })
                    findAndHookMethod("com.meizu.flyme.launcher.cm", loadPackageParam.classLoader, "b", ComponentName::class.java, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val componentName = param.args[0] as ComponentName
                            if (hideAppList.contains(componentName.packageName)) {
                                param.result = true
                            }
                        }
                    })
                }
            }
        }
    }

    override fun initZygote(startupParam: StartupParam?) {}
}