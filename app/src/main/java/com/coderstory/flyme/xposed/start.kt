package com.coderstory.flyme.xposed

import android.os.Build
import com.coderstory.flyme.patchModule.FlymeHome
import com.coderstory.flyme.patchModule.FlymeRoot
import com.coderstory.flyme.patchModule.FuckAd
import com.coderstory.flyme.patchModule.HideApp
import com.coderstory.flyme.patchModule.IsEnable
import com.coderstory.flyme.patchModule.Others
import com.coderstory.flyme.patchModule.SystemUi
import com.coderstory.flyme.patchModule.ThemePatcher
import com.coderstory.flyme.patchModule.corepatch.CorePatchForQ
import com.coderstory.flyme.patchModule.corepatch.CorePatchForR
import com.coderstory.flyme.tools.*
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.InvocationTargetException

class start : XposedHelper(), IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    override fun handleInitPackageResources(resparam: InitPackageResourcesParam) {
        if (Utils.Companion.vi()) {
            FlymeRoot().handleInitPackageResources(resparam)
            FlymeHome().handleInitPackageResources(resparam)
            Others().handleInitPackageResources(resparam)
            SystemUi().handleInitPackageResources(resparam)
        }
    }

    @Throws(IllegalAccessException::class, InstantiationException::class, InvocationTargetException::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (XposedHelper.Companion.json.isEmpty()) {
            initJson(lpparam)
        }
        if (Utils.Companion.vi()) {
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
    }

    override fun initZygote(startupParam: StartupParam) {
        //XposedBridge.log(" 产品有效期:" + Misc.endTime);
        //XposedBridge.log("激活状态:" + vi());
        //XposedBridge.log("SDK版本号: " + android.os.Build.VERSION.SDK_INT);
        //XposedBridge.log("exists" + new File("/data/user_de/0/" + ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml").exists());
        if (startupParam.startsSystemServer) {
            if (Build.VERSION.SDK_INT == 30) {
                CorePatchForR().initZygote(startupParam)
            } else if (Build.VERSION.SDK_INT == 29) {
                CorePatchForQ().initZygote(startupParam)
            } else {
                XposedBridge.log("Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
            // XposedBridge.log("当前助手配置 -> " + JSON.toJSON(prefs));
        }
    }
}