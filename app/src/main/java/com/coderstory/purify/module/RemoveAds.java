package com.coderstory.purify.module;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class RemoveAds extends XposedHelper implements IModule {

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        //屏蔽魅族广告sdk相关 splashad JsAdBridge  主题 安全中心 商店  日历  天气  音乐
        Class<?> clazz = findClassWithoutLog("com.meizu.advertise.api.JsAdBridge", loadPackageParam.classLoader);
        if (clazz != null) {
            Class<?> finalClazz = clazz;
            hookAllConstructors(clazz, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("发现广告sdk" + loadPackageParam.packageName);
                    XposedHelpers.setStaticObjectField(finalClazz, "OBJECT_NAME", "fuck_ad");
                }
            });
        }
        clazz = findClassWithoutLog("com.meizu.advertise.api.AdManager", loadPackageParam.classLoader);
        Class aa = findClassWithoutLog("com.meizu.advertise.api.AdView", loadPackageParam.classLoader);
//        if (clazz != null && aa != null) {
//            hookAllMethods(aa, "getData", XC_MethodReplacement.returnConstant(aa.newInstance()));
//        }
        if (aa != null) {
            findAndHookMethod(aa, "setPadding", int.class, int.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.args[0] = 0;
                    param.args[1] = 0;
                    param.args[2] = 0;
                    param.args[3] = 0;
                }
            });
        }

        clazz = findClassWithoutLog("com.meizu.advertise.api.AdLabelLayout", loadPackageParam.classLoader);
        if (clazz != null) {
            findAndHookMethod(clazz, "getLabelView", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    TextView textView = (TextView) param.getResult();
                    textView.setVisibility(View.GONE);
                    param.setResult(textView);
                }
            });
        }

        clazz = findClassWithoutLog("com.meizu.advertise.api.SimpleJsAdBridge", loadPackageParam.classLoader);
        if (clazz != null) {
            XposedBridge.log("发现广告sdk" + loadPackageParam.packageName);
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

}


