package com.coderstory.flyme10.xposed

import com.coderstory.flyme10.patchModule.FuckAd
import com.coderstory.flyme10.patchModule.HideApp
import com.coderstory.flyme10.patchModule.IsEnable
import com.coderstory.flyme10.patchModule.Others
import com.coderstory.flyme10.patchModule.SystemUi
import com.coderstory.flyme10.patchModule.ThemePatcher
import com.coderstory.flyme10.tools.XposedHelper
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Start : XposedHelper(), IXposedHookZygoteInit, IXposedHookLoadPackage,
    IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: InitPackageResourcesParam) {
        Others().handleInitPackageResources(resparam)
        SystemUi().handleInitPackageResources(resparam)
        ThemePatcher().handleInitPackageResources(resparam)
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (json.isEmpty()) {
            initJson(lpparam)
        }
        IsEnable().handleLoadPackage(lpparam)
        HideApp().handleLoadPackage(lpparam)
        Others().handleLoadPackage(lpparam)
        ThemePatcher().handleLoadPackage(lpparam)
        FuckAd().handleLoadPackage(lpparam)
        SystemUi().handleLoadPackage(lpparam)
    }

    override fun initZygote(startupParam: StartupParam) {
        XposedBridge.log("Flyme助手已加载")
        SystemUi().initZygote(startupParam)
    }
}