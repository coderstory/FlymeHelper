package com.coderstory.purify.plugins;


import com.coderstory.purify.module.CorePatch;
import com.coderstory.purify.module.FlymeHome;
import com.coderstory.purify.module.FlymeRoot;
import com.coderstory.purify.module.HideApp;
import com.coderstory.purify.module.IsEnable;
import com.coderstory.purify.module.Others;
import com.coderstory.purify.module.RemoveAds;
import com.coderstory.purify.module.ThemePatcher;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class start implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        new FlymeRoot().handleInitPackageResources(resparam);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        new IsEnable().handleLoadPackage(lpparam);
        new RemoveAds().handleLoadPackage(lpparam);
        new HideApp().handleLoadPackage(lpparam);
        new Others().handleLoadPackage(lpparam);
        new FlymeHome().handleLoadPackage(lpparam);
        new CorePatch().handleLoadPackage(lpparam);
        new ThemePatcher().handleLoadPackage(lpparam);
        new FlymeRoot().handleLoadPackage(lpparam);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log("Flyme7助手 3.x 开始Patch");
        new CorePatch().initZygote(startupParam);
        new ThemePatcher().initZygote(startupParam);
    }
}
