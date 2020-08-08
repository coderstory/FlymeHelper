package com.coderstory.flyme.module;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.os.Build;

import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.XposedHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String value2 = prefs.getString("Hide_App_Name_List", "");
                    final List<String> hideAppList = Arrays.asList(value2.split(":"));
                    //  public void addItem(Context context, com.android.launcher3.ItemInfo item, boolean newItem) {
                    // CharSequence title;
                    hookAllMethods("com.android.launcher3.model.BgDataModel", loadPackageParam.classLoader, "addItem", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            CharSequence title = (CharSequence) XposedHelpers.getObjectField(param.args[1], "title");
                            XposedBridge.log("GGG" + (title == null ? "null" : title.toString()));
                            if (title == null || !hideAppList.contains(title.toString())) {
                                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                            }
                            return null;
                        }
                    });

                } else {
                    final List<String> hideAppList = Arrays.asList(value.split(":"));
                    Class clazz = findClass("com.meizu.flyme.launcher.co", loadPackageParam.classLoader);
                    findAndHookMethod("com.meizu.flyme.launcher.MzWidgetGroupView", loadPackageParam.classLoader, "a", clazz, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            Object obj = param.args[0];
                            List<?> list = (List) XposedHelpers.getObjectField(obj, "a");
                            //XposedBridge.log("个数1" + list.size());
                            list = list.stream().filter(item -> value.contains(((AppWidgetProviderInfo) item).provider.getPackageName())).collect(Collectors.toList());
                            XposedHelpers.setObjectField(obj, "a", list);
                            //XposedBridge.log("个数2" + list.size());
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
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

    }
}
