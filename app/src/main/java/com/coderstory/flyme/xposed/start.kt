package com.coderstory.flyme.xposed

import android.os.Build
import com.coderstory.flyme.BuildConfig
import com.coderstory.flyme.patchModule.*
import com.coderstory.flyme.patchModule.corepatch.CorePatchForQ
import com.coderstory.flyme.patchModule.corepatch.CorePatchForR
import com.coderstory.flyme.tools.Utils
import com.coderstory.flyme.tools.XposedHelper
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class start : XposedHelper(), IXposedHookZygoteInit, IXposedHookLoadPackage,
    IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: InitPackageResourcesParam) {
        FlymeRoot().handleInitPackageResources(resparam)
        FlymeHome().handleInitPackageResources(resparam)
        Others().handleInitPackageResources(resparam)
        SystemUi().handleInitPackageResources(resparam)
        ThemePatcher().handleInitPackageResources(resparam)
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (json.isEmpty()) {
            initJson(lpparam)
        }

        FlymeHome().handleLoadPackage(lpparam)
        IsEnable().handleLoadPackage(lpparam)
        HideApp().handleLoadPackage(lpparam)
        Others().handleLoadPackage(lpparam)
        ThemePatcher().handleLoadPackage(lpparam)
        FlymeRoot().handleLoadPackage(lpparam)
        FuckAd().handleLoadPackage(lpparam)
        SystemUi().handleLoadPackage(lpparam)
        if ("android" == lpparam.packageName && lpparam.processName == "android") {
            if (Build.VERSION.SDK_INT == 30) {
                CorePatchForR().handleLoadPackage(lpparam)
            } else if (Build.VERSION.SDK_INT == 29) {
                CorePatchForQ().handleLoadPackage(lpparam)
            } else {
                XposedBridge.log("Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }

        }
    }

    override fun initZygote(startupParam: StartupParam) {
        XposedBridge.log("Flyme助手已加载")
        XposedBridge.log("version${BuildConfig.VERSION_NAME}")
        SystemUi().initZygote(startupParam)
        if (startupParam.startsSystemServer) {
            if (Build.VERSION.SDK_INT == 30) {
                CorePatchForR().initZygote(startupParam)
            } else if (Build.VERSION.SDK_INT == 29) {
                CorePatchForQ().initZygote(startupParam)
            } else {
                XposedBridge.log("Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
        }
    }
}