package com.coderstory.flyme.patchModule


import android.content.Context
import android.webkit.WebView
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam


class FuckAd : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {}
    override fun initZygote(startupParam: StartupParam?) {}
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if ((loadPackageParam.packageName.contains("meizu") ||
                        loadPackageParam.packageName.contains("flyme") || loadPackageParam.packageName.contains(
                        "mz"
                )) &&
                prefs.getBoolean("EnableBlockAD", false)) {
            // 处理内嵌网页上的广告  例如天气中的15日天气
            var clazz = findClassWithoutLog("com.meizu.advertise.api.JsAdBridge", loadPackageParam.classLoader)
            if (clazz != null) {
                val finalClazz = clazz
                hookAllConstructors(clazz, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.beforeHookedMethod(param)
                        XposedHelpers.setStaticObjectField(finalClazz, "OBJECT_NAME", "fuck_ad")
                    }
                })
            }

            // 禁止app加载魅族的广告插件 com.meizu.advertisef,..plugin.apk
            clazz = findClassWithoutLog("com.meizu.advertise.api.AdManager", loadPackageParam.classLoader)
            if (clazz != null) {
                hookAllMethods(clazz, "installPlugin", XC_MethodReplacement.returnConstant(null))
                hookAllMethods(clazz, "install", XC_MethodReplacement.returnConstant(null))
                hookAllMethods(clazz, "init", XC_MethodReplacement.returnConstant(null))
            }
            clazz = findClassWithoutLog("com.meizu.dynamic.PluginManager", loadPackageParam.classLoader)
            if (clazz != null) {
                hookAllMethods(clazz, "install", XC_MethodReplacement.returnConstant(null))
                hookAllMethods(clazz, "installFromDownload", XC_MethodReplacement.returnConstant(null))
                hookAllMethods(clazz, "newContext", XC_MethodReplacement.returnConstant(true))
                hookAllMethods(clazz, "isFirstInstalled", XC_MethodReplacement.returnConstant(true))
            }
            clazz = findClassWithoutLog("com.meizu.advertise.update.PluginManager", loadPackageParam.classLoader)
            if (clazz != null) {
                hookAllMethods(clazz, "install", XC_MethodReplacement.returnConstant(null))
                hookAllMethods(clazz, "isFirstInstalled", XC_MethodReplacement.returnConstant(true))
                hookAllMethods(clazz, "newContext", XC_MethodReplacement.returnConstant(true))
                hookAllMethods(clazz, "installFromDownload", XC_MethodReplacement.returnConstant(null))
            }

            clazz = findClassWithoutLog("com.meizu.advertise.api.SimpleJsAdBridge", loadPackageParam.classLoader)
            if (clazz != null) {
                XposedHelpers.findAndHookConstructor(clazz, Context::class.java, WebView::class.java, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        // super(activity, new SimpleWebView(webView));
                        // webView.addJavascriptInterface(this, JsAdBridge.OBJECT_NAME);
                        // this.mWebView = webView;
                        val webView = XposedHelpers.getObjectField(param.thisObject, "mWebView") as WebView
                        webView.removeJavascriptInterface("mzAd")
                    }
                })
            }
            if (findClassWithoutLog("com.meizu.flyme.media.news.lite.NewsFullManager", loadPackageParam.classLoader) != null) {
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "fetchAdData",
                    XC_MethodReplacement.returnConstant(null)
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "doFetchMeizuAdData",
                    XC_MethodReplacement.returnConstant(null)
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "fetchArticlesWithRetry",
                    XC_MethodReplacement.returnConstant(false)
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "fetchManualArticles",
                    XC_MethodReplacement.returnConstant(false)
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "fetchTopics",
                    XC_MethodReplacement.returnConstant(false)
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "getNormalArticlesFromCache",
                    XC_MethodReplacement.returnConstant(ArrayList<String>())
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "fetchTopics",
                    XC_MethodReplacement.returnConstant(false)
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "fetchSpecialArticlesConcurrently",
                    XC_MethodReplacement.returnConstant(false)
                )
                hookAllMethods(
                    "com.meizu.flyme.media.news.lite.NewsFullManager",
                    loadPackageParam.classLoader,
                    "fetchArticles",
                    XC_MethodReplacement.returnConstant(false)
                )
            }

            if (loadPackageParam.packageName == "com.hy.weather.mz") {
                XposedBridge.log("wqeqwewqe")
                hookAllMethods(
                    "com.hy.weather.mz.cityWeather.WeatherInfoRepository",
                    loadPackageParam.classLoader,
                    "getForecastFromServer",
                    XC_MethodReplacement.returnConstant(null)
                )
                hookAllMethods(
                    "com.hy.weather.mz.cityWeather.WeatherInfoRepository",
                    loadPackageParam.classLoader,
                    "getForecastFromCache",
                    XC_MethodReplacement.returnConstant(null)
                )
            }

        }
        if (loadPackageParam.packageName == "com.android.packageinstaller") {
            if (prefs.getBoolean("removeStore", false)) {
                hookAllMethods("com.meizu.safe.security.net.HttpMethods", loadPackageParam.classLoader, "queryPackageInfoFromMzStoreV2", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.beforeHookedMethod(param)
                        param.args[1] = "xxxx"
                        param.args[3] = "xxxx"
                        param.args[6] = "xxxx"
                    }
                })
            }
            if (prefs.getBoolean("autoInstall", false)) {
                // 开启会自动安装apk
                hookAllMethods("com.meizu.permissioncommon.AppInfoUtil", loadPackageParam.classLoader, "isSystemApp", XC_MethodReplacement.returnConstant(true))
            }
        }
        /**
         * public void init(Object obj, Map<String></String>, String> map) {
         * try {
         * super.init(obj, map);
         * String str = (String) map.get("CHANNEL");
         * String str2 = (String) map.get("SIM_ICCID");
         * ParseManager.setSdkDoAction((AbsSdkDoAction) obj);
         * ParseManager.initSdk(this.mContext, str, str2, true, true, map);
         * this.mSdkInit = true;
         * } catch (Throwable th) {
         * Log.e(TAG, "init", th);
         * }
         * }
         */
        if (loadPackageParam.packageName == "com.android.mms") {
            if (prefs.getBoolean("mms", false)) {
                hookAllMethods("com.xy.smartsms.pluginxy.XYSmsPlugin", loadPackageParam.classLoader, "init", XC_MethodReplacement.returnConstant(null))
            }
        }
    }
}