package com.coderstory.purify.module;

import android.content.Context;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.SharedHelper;
import com.coderstory.purify.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class FlymeRoot extends XposedHelper implements IModule {
    private SharedHelper helper;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class<?> ContextClass = findClass("android.content.ContextWrapper", loadPackageParam.classLoader);
        findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (helper != null)
                    return;
                helper = new SharedHelper((Context) param.getResult());
                XposedBridge.log("得到上下文");
            }
        });
        if (loadPackageParam.packageName.equals("com.meizu.mznfcpay") && helper.getBoolean("HideRootWithPay", false)) {
            findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "c", Context.class, XC_MethodReplacement.returnConstant(false));
        }
        if (loadPackageParam.packageName.equals("com.meizu.flyme.update") && helper.getBoolean("HideRootWithUpgrade", false)) {
            // DEVICE_STATE_SERVICE
            if (findClassWithoutLog("com.meizu.cloud.a.a.a", loadPackageParam.classLoader) != null) {
                findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context.class, XC_MethodReplacement.returnConstant(false));
            }
            findAndHookMethod("com.meizu.cloud.a.b.a", loadPackageParam.classLoader, "c", Context.class, XC_MethodReplacement.returnConstant(false));
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) {
    }
}
