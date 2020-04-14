package com.coderstory.purify.module;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.webkit.WebView;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.SharedHelper;
import com.coderstory.purify.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;



public class RemoveAds extends XposedHelper implements IModule {
    private SharedHelper helper = new SharedHelper(AndroidAppHelper.currentApplication().getApplicationContext());
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if (!loadPackageParam.packageName.contains("meizu") &&
                !loadPackageParam.packageName.contains("flyme") &&
                !helper.getBoolean("EnableBlockAD", false)) {
            return;
        }

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

        // 禁止app加载魅族的广告插件 com.meizu.advertise.plugin.apk
        clazz = findClassWithoutLog("com.meizu.advertise.api.AdManager", loadPackageParam.classLoader);
        if (clazz != null) {
            findAndHookMethod(clazz, "installPlugin", XC_MethodReplacement.returnConstant(null));
        }

    }

}


