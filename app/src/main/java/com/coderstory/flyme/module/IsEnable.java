package com.coderstory.flyme.module;


import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class IsEnable extends XposedHelper implements IModule {


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.coderstory.flyme")) {
            findAndHookMethod("com.coderstory.flyme.activity.MainActivity", lpparam.classLoader, "isEnable", XC_MethodReplacement.returnConstant(true));
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam paramStartupParam) {
    }

}
