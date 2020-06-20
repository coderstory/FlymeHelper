package com.coderstory.flyme.module;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;

import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.Dex2C;
import com.coderstory.flyme.utils.XposedHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@Dex2C
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
                Class clazz = findClass("com.meizu.flyme.launcher.co", loadPackageParam.classLoader);
                findAndHookMethod("com.meizu.flyme.launcher.MzWidgetGroupView", loadPackageParam.classLoader, "a", clazz, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object obj = param.args[0];
                        List<?> list = (List) XposedHelpers.getObjectField(obj, "a");
                        XposedBridge.log("个数1"+list.size());
                        list = list.stream().filter(item -> value.contains(((AppWidgetProviderInfo) item).provider.getPackageName())).collect(Collectors.toList());
                        XposedHelpers.setObjectField(obj, "a", list);
                        XposedBridge.log("个数2"+list.size());
                    }
                });

                findAndHookMethod("com.meizu.flyme.launcher.cm", loadPackageParam.classLoader, "b", ComponentName.class, new XC_MethodHook() {
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
