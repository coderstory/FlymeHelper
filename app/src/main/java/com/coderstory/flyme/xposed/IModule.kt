package com.coderstory.flyme.xposed

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.InvocationTargetException

interface IModule {
    fun handleInitPackageResources(resparam: InitPackageResourcesParam)

    @Throws(IllegalAccessException::class, InvocationTargetException::class, InstantiationException::class)
    fun handleLoadPackage(lpparam: LoadPackageParam)
    fun initZygote(startupParam: StartupParam?)
}