package com.coderstory.flyme.xposed

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

interface IModule {
    fun handleInitPackageResources(resparam: InitPackageResourcesParam)
    fun handleLoadPackage(lpparam: LoadPackageParam)
    fun initZygote(startupParam: StartupParam?)
}