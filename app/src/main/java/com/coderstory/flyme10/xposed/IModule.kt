package com.coderstory.flyme10.xposed

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

interface IModule {
    fun handleInitPackageResources(respray: InitPackageResourcesParam)
    fun handleLoadPackage(param: LoadPackageParam)
    fun initZygote(startupParam: StartupParam?)
}