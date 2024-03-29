package com.coderstory.flyme10.patchModule


import com.coderstory.flyme10.tools.XposedHelper
import com.coderstory.flyme10.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class IsEnable : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {}
    override fun handleLoadPackage(param: LoadPackageParam) {
        if (param.packageName == "com.coderstory.flyme10") {
            findAndHookMethod(
                "com.coderstory.flyme10.activity.MainActivity",
                param.classLoader,
                "isEnable",
                XC_MethodReplacement.returnConstant(true)
            )
        }
    }

    override fun initZygote(startupParam: StartupParam?) {}
}