package com.coderstory.purify.module;

import android.content.ComponentName;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;



public class HideApp extends XposedHelper implements IModule {

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {


        if (loadPackageParam.packageName.equals("com.meizu.flyme.launcher")) {
            // bl.add(new ComponentName("com.android.vending", "com.android.vending.MarketWidgetProvider"));
            final String value = prefs.getString("Hide_App_List", "");
            XposedBridge.log("load config" + value);
            if (!value.equals("")) {
                final List<String> hideAppList = Arrays.asList(value.split(":"));
                XposedBridge.log("load config" + value);
                // 下面2个hook是一个东西
                findAndHookMethod("com.meizu.flyme.launcher.ck", loadPackageParam.classLoader, "a", ComponentName.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        ComponentName componentName = (ComponentName) param.args[0];
                        if (hideAppList.contains(componentName.getPackageName())) {
                            param.setResult(true);
                        }
                    }
                });
                findAndHookMethod("com.meizu.flyme.launcher.ck", loadPackageParam.classLoader, "b", ComponentName.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        ComponentName componentName = (ComponentName) param.args[0];
                        if (hideAppList.contains(componentName.getPackageName())) {
                            param.setResult(true);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

    }
}
