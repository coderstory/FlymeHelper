package com.coderstory.flyme.module;

import android.content.Context;
import android.webkit.WebView;

import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.Dex2C;
import com.coderstory.flyme.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@Dex2C
public class RemoveAds extends XposedHelper implements IModule {


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if ((loadPackageParam.packageName.contains("meizu") ||
                loadPackageParam.packageName.contains("flyme")) &&
                prefs.getBoolean("EnableBlockAD", false)) {
            // 处理内嵌网页上的广告  例如天气中的15日天气
            Class<?> clazz = findClassWithoutLog("com.meizu.advertise.api.JsAdBridge", loadPackageParam.classLoader);
            if (clazz != null) {
                Class<?> finalClazz = clazz;
                hookAllConstructors(clazz, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedHelpers.setStaticObjectField(finalClazz, "OBJECT_NAME", "fuck_ad");
                    }
                });
            }
            // 禁止app加载魅族的广告插件 com.meizu.advertise.plugin.apk
            clazz = findClassWithoutLog("com.meizu.advertise.api.AdManager", loadPackageParam.classLoader);
            if (clazz != null) {
                findAndHookMethod(clazz, "installPlugin", XC_MethodReplacement.returnConstant(null));
            }
            // com.meizu.advertise.update.install(Context context, InstallConfig installConfig) 8.8.52
            clazz = findClassWithoutLog("com.meizu.advertise.update.PluginManager", loadPackageParam.classLoader);
            if (clazz != null) {
                hookAllMethods(clazz, "install", XC_MethodReplacement.returnConstant(null));
            }
            clazz = findClassWithoutLog("com.meizu.advertise.api.SimpleJsAdBridge", loadPackageParam.classLoader);
            if (clazz != null) {
                XposedHelpers.findAndHookConstructor(clazz, Context.class, WebView.class, new XC_MethodHook() {
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


        if (loadPackageParam.packageName.equals("com.android.packageinstaller")) {
            if (prefs.getBoolean("removeStore", false)) {
                hookAllMethods("com.meizu.safe.security.net.HttpMethods", loadPackageParam.classLoader, "queryPackageInfoFromMzStoreV2", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[1] = "xxxx";
                        param.args[3] = "xxxx";
                        param.args[6] = "xxxx";
                    }
                });
            }
            if (prefs.getBoolean("autoInstall", false)) {
                // 开启会自动安装apk
                hookAllMethods("com.meizu.permissioncommon.AppInfoUtil", loadPackageParam.classLoader, "isSystemApp", XC_MethodReplacement.returnConstant(true));
            }
        }

        /**
         *     public void init(Object obj, Map<String, String> map) {
         *         try {
         *             super.init(obj, map);
         *             String str = (String) map.get("CHANNEL");
         *             String str2 = (String) map.get("SIM_ICCID");
         *             ParseManager.setSdkDoAction((AbsSdkDoAction) obj);
         *             ParseManager.initSdk(this.mContext, str, str2, true, true, map);
         *             this.mSdkInit = true;
         *         } catch (Throwable th) {
         *             Log.e(TAG, "init", th);
         *         }
         *     }
         */
        if (loadPackageParam.packageName.equals("com.android.mms")) {
            if (prefs.getBoolean("mms", false)) {
                hookAllMethods("com.xy.smartsms.pluginxy.XYSmsPlugin", loadPackageParam.classLoader, "init", XC_MethodReplacement.returnConstant(null));
            }
        }
    }

}

