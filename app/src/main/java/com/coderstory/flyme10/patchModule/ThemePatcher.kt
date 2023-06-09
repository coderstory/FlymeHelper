package com.coderstory.flyme10.patchModule


import android.content.*
import android.net.Uri
import com.coderstory.flyme10.tools.XposedHelper
import com.coderstory.flyme10.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam


class ThemePatcher : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {
        if (respray.packageName == "com.meizu.customizecenter" && prefs.getBoolean(
                "enabletheme",
                false
            )
        ) {
            if (respray.packageName == "com.meizu.customizecenter") {
                XposedBridge.log("开始替换文本")
                respray.res.setReplacement(0x7f1102ec, "开始白嫖")
                respray.res.setReplacement(0x7f1102e5, "开始白嫖")
                respray.res.setReplacement(0x7f1102f2, "正在白嫖")
            }
        }
    }
    override fun handleLoadPackage(param: LoadPackageParam) {

        // 主题和谐
        if (param.packageName == "com.meizu.customizecenter" && prefs.getBoolean(
                "enabletheme",
                false
            )
        ) {
            if (param.packageName == "com.meizu.customizecenter") {
                XposedBridge.log("开始hook主题")
                // 开始试用主题
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.theme.f",
                    param.classLoader,
                    "N",
                    String::class.java,
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(null)
                )

                // 拦截开机自启广播
                findAndHookMethod(
                    "com.meizu.customizecenter.admin.receiver.BootBroadcastReceiver",
                    param.classLoader,
                    "onReceive",
                    Context::class.java,
                    Intent::class.java,
                    XC_MethodReplacement.returnConstant(null)
                )

                // 拦截试用服务
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.font.FontTrialService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(2)
                )
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.theme.ThemeTrialService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    object : XC_MethodReplacement() {
                        @Throws(Throwable::class)
                        override fun replaceHookedMethod(param: MethodHookParam): Any {
                            return 2
                        }
                    })

                //resetToSystemTheme
                // findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.common.b", lpparam.classLoader, "c", XC_MethodReplacement.returnConstant(true))
                // 10.1.x
                if (findClassWithoutLog(
                        "com.meizu.flyme10.policy.sdk.ve0",
                        param.classLoader
                    ) != null
                ) {
                    findAndHookMethod(
                        "com.meizu.flyme10.policy.sdk.ve0",
                        param.classLoader,
                        "J",
                        XC_MethodReplacement.returnConstant(true)
                    )
                } else {
                    // 10.2.2
                    findAndHookMethod(
                        "com.meizu.flyme10.policy.sdk.pe0",
                        param.classLoader,
                        "J",
                        XC_MethodReplacement.returnConstant(true)
                    )
                }


                /**
                 *
                 * public void a(boolean arg4, boolean arg5) {
                 * com.meizu.customizecenter.manager.utilstool.c.b.b(this.b, "startFontRestoreService");
                 * Intent v0 = new Intent(this.d, FontRestoreService.class);
                 * v0.putExtra(com.meizu.customizecenter.model.a.a$g.h.a(), this.e().a());
                 * v0.putExtra("is_go_to_pay_font", arg5);
                 * v0.putExtra("is_restore_last_key", arg4);
                 * com.meizu.customizecenter.manager.utilstool.systemutills.a.a.a(this.d, v0);
                 * }
                 */
                // 7.5
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.font.k",
                    param.classLoader,
                    "Y",
                    Boolean::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(null)
                )


                /**
                 * public void k() {
                 * long v0 = SystemClock.elapsedRealtime() - this.n();
                 * com.meizu.customizecenter.manager.utilstool.c.b.e(this.b, "checkTrialFontWhenAppStart_interval:" + v0 / 1000 + " second");
                 * if(!this.f(v0)) {
                 * this.g();
                 * }
                 * }
                 */
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.font.k",
                    param.classLoader,
                    "m",
                    XC_MethodReplacement.returnConstant(null)
                )


                //"checkTrialFont:!isUsingTrialFont() Context context, String str, long j
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.managermoduls.font.k",
                    param.classLoader,
                    "l",
                    Context::class.java,
                    String::class.java,
                    Long::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(null)
                )

                val themeContentProvider: Class<*> = findClass(
                    "com.meizu.customizecenter.manager.utilshelper.dbhelper.dao.ThemeContentProvider",
                    param.classLoader
                )
                //主题混搭 ThemeContentProvider query Unknown URI
                findAndHookMethod(
                    themeContentProvider,
                    "query",
                    Uri::class.java,
                    Array<String>::class.java,
                    String::class.java,
                    Array<String>::class.java,
                    String::class.java,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val objs = param.args
                            val tag = "(ITEMS LIKE"
                            val tag2 = "%zklockscreen;%"
                            val tag3 = "%com.meizu.flyme10.weather;%"
                            var result = false
                            for (obj in objs) {
                                if (obj is String && (obj.contains(tag) || obj == tag2 || obj == tag3)) {
                                    result = true
                                }
                            }
                            if (result) {
                                for (obj in objs) {
                                    if (obj is Array<*>) { // obj is String[]
                                        for (j in (obj as Array<String>).indices) {
                                            if (obj[j].contains("/storage/emulated/0/Customize/Themes")) {
                                                obj[j] = "/storage/emulated/0/Customize%"
                                            } else if (obj[j].contains("/storage/emulated/0/Customize/TrialThemes")) {
                                                obj[j] = "NONE"
                                            }
                                        }
                                    }
                                }
                            }
                            super.beforeHookedMethod(param)
                        }
                    })

                // android 11 8.30.2
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.utilshelper.restorehelper.ThemeRestoreService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(0)
                )
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.utilshelper.restorehelper.FontRestoreService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(0)
                )
                findAndHookMethod(
                    "com.meizu.customizecenter.manager.utilshelper.scannerhelper.CustomizeScannerService",
                    param.classLoader,
                    "onStartCommand",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    XC_MethodReplacement.returnConstant(0)
                )
            }
        }
    }

    override fun initZygote(startupParam: StartupParam?) {
        TODO("Not yet implemented")
    }


}