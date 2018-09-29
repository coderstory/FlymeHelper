package com.coderstory.FTool.module;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.coderstory.FTool.plugins.IModule;
import com.coderstory.FTool.utils.XposedHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hooks extends XposedHelper implements IModule {


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XSharedPreferences prefs = new XSharedPreferences("com.coderstory.FTool", "com.coderstory.FTool_preferences");
        prefs.makeWorldReadable();
        prefs.reload();
        Class clazz;
        if (prefs.getBoolean("enableAd", false)) {
            //屏蔽魅族广告sdk相关 splashad JsAdBridge
            clazz = findclass("com.meizu.advertise.api.JsAdBridge", lpparam.classLoader);
            if (clazz != null) {
                Class<?> finalClazz = clazz;
                hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("发现广告sdk" + lpparam.packageName);
                        XposedHelpers.setStaticObjectField(finalClazz, "OBJECT_NAME", "fuck_ad");
                    }
                });
            }
            clazz = findclass("com.meizu.advertise.api.AdManager", lpparam.classLoader);
            if (clazz != null) {
                XposedBridge.log("发现广告sdk" + lpparam.packageName);
                Class<?> finalClazz = clazz;
                hookAllMethods(clazz, "getData", XC_MethodReplacement.returnConstant(null));
            }

            clazz = findclass("com.meizu.advertise.api.SimpleJsAdBridge", lpparam.classLoader);
            if (clazz != null) {
                XposedBridge.log("发现广告sdk" + lpparam.packageName);
                Class<?> finalClazz = clazz;
                XposedHelpers.findAndHookConstructor(clazz, Activity.class, WebView.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //super(activity, new SimpleWebView(webView));
                        // webView.addJavascriptInterface(this, JsAdBridge.OBJECT_NAME);
                        // this.mWebView = webView;
                        WebView webView = (WebView) XposedHelpers.getObjectField(param.thisObject, "mWebView");
                        webView.removeJavascriptInterface("mzAd");
                    }
                });
            }
        }

        // 自定义桌面布局 com.meizu.flyme.launcher下第一个参数是String 并包含了很多个入参
        if (lpparam.packageName.equals("com.meizu.flyme.launcher")) {

            clazz = findclass("com.meizu.flyme.launcher.u", lpparam.classLoader);
            if (clazz == null) {
                // 7.x
                clazz = findclass("com.meizu.flyme.launcher.v", lpparam.classLoader);
            }
            // 开启自定义布局
            if (prefs.getBoolean("launcherMMO", false)) {
                hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("开启自定义布局");
                        if (param.args[0].getClass().equals(String.class)) {
                            param.args[3] = 5.0f;
                            param.args[4] = 5.0f;
                        }
                    }
                });
            }


            // 不同布局使用不同的db
            hookAllConstructors(SQLiteOpenHelper.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam hookParam) {
                    if ("launcher.db".equals(hookParam.args[1])) {
                        Object arg = hookParam.args[0];
                        if (arg != null) {
                            String dbName = "launcher_coderStory.db";
                            XposedHelpers.setObjectField(hookParam.thisObject, "mName", dbName);

                            File file = ((Context) arg).getDatabasePath("launcher.db");
                            if (file != null && (file.exists())) {
                                File databasePath = ((Context) arg).getDatabasePath(dbName);
                                if (databasePath != null && (databasePath.exists())) {
                                    return;
                                }
                                writeFile(file, databasePath);
                            }
                        }
                    }
                }
            });


            // 隐藏图标标签
            if (prefs.getBoolean("HideName", false)) {
                hookAllMethods(findclass("com.meizu.flyme.launcher.ShortcutIcon", lpparam.classLoader), "a", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        TextView textView = (TextView) XposedHelpers.getObjectField(param.thisObject, "c");
                        if (textView != null) {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            final String value = prefs.getString("Hide_App_List", "");
            if (!value.equals("")) {
                final List<String> hideAppList = Arrays.asList(value.split(":"));
                    XposedBridge.log("load config" + value);
                    findAndHookMethod("com.meizu.flyme.launcher.ca", lpparam.classLoader, "b", ComponentName.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            ComponentName componentName = (ComponentName) param.args[0];
                            if (hideAppList.contains(componentName.getPackageName())) {
                                XposedBridge.log("hide app " + componentName.getPackageName());
                                param.setResult(true);
                            }
                        }
                    });
            }

            // 隐藏图标 参数ComponentName  返回bool
            // findAndHookMethod("com.meizu.flyme.launcher.ai", lpparam.classLoader, "a",ComponentName.class, XC_MethodReplacement.returnConstant(true));
            //findAndHookMethod("com.meizu.flyme.launcher.ca", lpparam.classLoader, "b", ComponentName.class, XC_MethodReplacement.returnConstant(true));
        }

        // 禁止安装app时候的安全检验
        if (lpparam.packageName.equals("com.android.packageinstaller")) {

            if (prefs.getBoolean("enableCheckInstaller", false)) {
                // 8.x
                clazz = findclass("com.android.packageinstaller.FlymePackageInstallerActivity", lpparam.classLoader);
                if (clazz == null) {
                    // 7.x
                    clazz = findclass("com.android.packageinstaller.PackageInstallerActivity", lpparam.classLoader);
                }
                if (clazz != null) {
                    findAndHookMethod(clazz, "setVirusCheckTime", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            Object mHandler = XposedHelpers.getObjectField(param.thisObject, "mHandler");
                            XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 5);
                            return null;
                        }
                    });
                }
            }
            if (prefs.getBoolean("enableCTS", false)) {
                XposedBridge.log("开启原生安装器");
                findAndHookMethod("com.meizu.safe.security.utils.Utils", lpparam.classLoader, "isCtsRunning", XC_MethodReplacement.returnConstant(true));
            }
        }

        // 隐藏root
        if (lpparam.packageName.equals("com.meizu.mznfcpay") && prefs.getBoolean("hideRootWithMeiZuPay", false)) {
            findAndHookMethod("com.meizu.cloud.a.a.a", lpparam.classLoader, "c", Context.class, XC_MethodReplacement.returnConstant(false));
        }
        if (lpparam.packageName.equals("com.meizu.flyme.update") && prefs.getBoolean("hideRootWithUpdater", false)) {
            // DEVICE_STATE_SERVICE
            findAndHookMethod("com.meizu.cloud.a.a.a", lpparam.classLoader, "b", Context.class, XC_MethodReplacement.returnConstant(false));
        }

        // 主题和谐
        if (lpparam.packageName.equals("com.meizu.customizecenter") && prefs.getBoolean("enableThemePatch", true)) {

            if (lpparam.packageName.equals("com.meizu.customizecenter")) {
                // 拦截开机自启广播
                findAndHookMethod("com.meizu.customizecenter.admin.receiver.BootBroadcastReceiver", lpparam.classLoader, "onReceive", Context.class, Intent.class, XC_MethodReplacement.returnConstant(null));

                // 拦截试用服务
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.FontTrialService", lpparam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.ThemeTrialService", lpparam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));

                //device_states | doCheckState
                //7.1.2
                findAndHookMethod("com.meizu.customizecenter.manager.utilstool.a.a", lpparam.classLoader, "e", Context.class, XC_MethodReplacement.returnConstant(0));

                //resetToSystemTheme
                //7.1.2
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.common.b", lpparam.classLoader, "c", XC_MethodReplacement.returnConstant(true));
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.common.b", lpparam.classLoader, "b", XC_MethodReplacement.returnConstant(true));

                //data/data/com.meizu.customizecenter/font/   system_font
                //7.1.2
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.c", lpparam.classLoader, "g", XC_MethodReplacement.returnConstant(""));

                //主题混搭 ThemeContentProvider query Unknown URI
                findAndHookMethod("com.meizu.customizecenter.manager.utilstool.dbutils.dao.ThemeContentProvider", lpparam.classLoader, "query", Uri.class, String[].class, String.class, String[].class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        Object[] objs = param.args;
                        String Tag = "(ITEMS LIKE";
                        String Tag2 = "%zklockscreen;%";
                        String Tag3 = "%com.meizu.flyme.weather;%";
                        //XposedBridge.log("开始");
                        boolean result = false;
                        for (Object obj : objs) {
                            //XposedBridge.log(obj == null ? "" : obj.toString());
                            if (obj instanceof String && (((String) obj).contains(Tag) || obj.equals(Tag2) || obj.equals(Tag3))) {
                                result = true;
                            }
                        }
                        //XposedBridge.log("结束");
                        if (result) {
                            for (Object obj : objs) {
                                if (obj instanceof String[]) {
                                    for (int j = 0; j < ((String[]) obj).length; j++) {
                                        if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/Themes")) {
                                            ((String[]) obj)[j] = "/storage/emulated/0/Customize%";
                                        } else if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/TrialThemes")) {
                                            ((String[]) obj)[j] = "NONE";
                                        }
                                    }
                                }
                            }
                        }
                        super.beforeHookedMethod(param);
                    }
                });
            }
        }

    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }
}


