package com.coderstory.FTool.plugins;


import com.coderstory.FTool.module.Hooks;
import com.coderstory.FTool.module.isEnable;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class start implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {


    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {


            new Hooks().handleLoadPackage(lpparam);
            new isEnable().handleLoadPackage(lpparam);

    }
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

    }
}
