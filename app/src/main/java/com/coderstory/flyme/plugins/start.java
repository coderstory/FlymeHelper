package com.coderstory.flyme.plugins;

import android.os.Build;

import com.coderstory.flyme.BuildConfig;
import com.coderstory.flyme.module.FlymeHome;
import com.coderstory.flyme.module.FlymeRoot;
import com.coderstory.flyme.module.HideApp;
import com.coderstory.flyme.module.IsEnable;
import com.coderstory.flyme.module.Others;
import com.coderstory.flyme.module.RemoveAds;
import com.coderstory.flyme.module.SystemUi;
import com.coderstory.flyme.module.ThemePatcher;
import com.coderstory.flyme.module.corepatch.CorePatchForQ;
import com.coderstory.flyme.module.corepatch.CorePatchForR;
import com.coderstory.flyme.utils.Utils;
import com.coderstory.flyme.utils.XposedHelper;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class start extends XposedHelper implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        if (Utils.vi()) {
            new FlymeRoot().handleInitPackageResources(resparam);
            new FlymeHome().handleInitPackageResources(resparam);
            new Others().handleInitPackageResources(resparam);
            new SystemUi().handleInitPackageResources(resparam);
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (json.isEmpty()) {
            initJson(lpparam);
        }
        XposedBridge.log("Flyme助手 " + BuildConfig.VERSION_NAME + " 开始Patch" + lpparam.packageName);
        if (Utils.vi()) {
            new FlymeHome().handleLoadPackage(lpparam);
            new IsEnable().handleLoadPackage(lpparam);
            new HideApp().handleLoadPackage(lpparam);
            new Others().handleLoadPackage(lpparam);
            new ThemePatcher().handleLoadPackage(lpparam);
            new FlymeRoot().handleLoadPackage(lpparam);
            new RemoveAds().handleLoadPackage(lpparam);
            new SystemUi().handleLoadPackage(lpparam);

            if (("android".equals(lpparam.packageName)) && (lpparam.processName.equals("android"))) {
                if (Build.VERSION.SDK_INT == 30) {
                    new CorePatchForR().handleLoadPackage(lpparam);
                } else if (Build.VERSION.SDK_INT == 29) {
                    new CorePatchForQ().handleLoadPackage(lpparam);
                } else {
                    XposedBridge.log("Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT);
                }
            }
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        //XposedBridge.log(" 产品有效期:" + Misc.endTime);
        //XposedBridge.log("激活状态:" + vi());
        //XposedBridge.log("SDK版本号: " + android.os.Build.VERSION.SDK_INT);
        //XposedBridge.log("exists" + new File("/data/user_de/0/" + ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml").exists());
        if (startupParam.startsSystemServer) {
            if (Build.VERSION.SDK_INT == 30) {
                new CorePatchForR().initZygote(startupParam);
            } else if (Build.VERSION.SDK_INT == 29) {
                new CorePatchForQ().initZygote(startupParam);
            } else {
                XposedBridge.log("Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT);
            }
            XposedBridge.log("当前助手配置 -> " + JSON.toJSON(prefs));
        }
    }
}
