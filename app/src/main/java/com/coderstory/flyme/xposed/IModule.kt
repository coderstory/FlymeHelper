package com.coderstory.flyme.xposed;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface IModule {
    void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam);

    void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws IllegalAccessException, InvocationTargetException, InstantiationException;

    void initZygote(IXposedHookZygoteInit.StartupParam startupParam);
}
