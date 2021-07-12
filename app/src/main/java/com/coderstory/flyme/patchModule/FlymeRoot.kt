package com.coderstory.flyme.patchModule

import android.content.Context
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class FlymeRoot : XposedHelper(), IModule {
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (loadPackageParam.packageName == "com.meizu.mznfcpay" && prefs.getBoolean("HideRootWithPay", false)) {
            // 6.0.7
            XposedHelper.Companion.findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context::class.java, XC_MethodReplacement.returnConstant(false))
        }
        if (loadPackageParam.packageName == "com.meizu.flyme.update" && prefs.getBoolean("HideRootWithUpgrade", false)) {
            // DEVICE_STATE_SERVICE
            if (findClassWithoutLog("com.meizu.cloud.a.a.a", loadPackageParam.classLoader) != null) {
                XposedHelper.Companion.findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context::class.java, XC_MethodReplacement.returnConstant(false))
            } else {
                XposedHelper.Companion.findAndHookMethod("com.meizu.cloud.a.b.a", loadPackageParam.classLoader, "c", Context::class.java, XC_MethodReplacement.returnConstant(false))
            }
        }

        // hook 框架层的root检测
        if ("android" == loadPackageParam.packageName) {
            XposedHelper.Companion.hookAllMethods("com.android.server.DeviceStateService", loadPackageParam.classLoader, "doCheckState", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val code = param.args[0] as Int
                    if (prefs.getBoolean("HideRootGlobal", false)) {
                        if (code == 1 || code == 3 || code == 4) {
                            param.result = 0
                        }
                    }
                }
            })
        }
    }

    override fun initZygote(startupParam: StartupParam?) {}
    override fun handleInitPackageResources(initPackageResourcesParam: InitPackageResourcesParam) {}
}