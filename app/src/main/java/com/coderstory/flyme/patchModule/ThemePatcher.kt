package com.coderstory.flyme.patchModule


import android.content.*
import android.net.Uri
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class ThemePatcher : XposedHelper(), IModule {
    override fun handleInitPackageResources(resparam: InitPackageResourcesParam) {}
    override fun handleLoadPackage(lpparam: LoadPackageParam) {

        // 主题和谐
        if (lpparam.packageName == "com.meizu.customizecenter" && prefs.getBoolean("enabletheme", false)) {
            if (lpparam.packageName == "com.meizu.customizecenter") {
                // 拦截开机自启广播
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.admin.receiver.BootBroadcastReceiver", lpparam.classLoader, "onReceive", Context::class.java, Intent::class.java, XC_MethodReplacement.returnConstant(null))

                // 拦截试用服务
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.FontTrialService", lpparam.classLoader, "onStartCommand", Intent::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, XC_MethodReplacement.returnConstant(2))
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.ThemeTrialService", lpparam.classLoader, "onStartCommand", Intent::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam): Any {
                        return 2
                    }
                })

                //device_states | doCheckState
                // 8.0.23
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.k.c.a.c", lpparam.classLoader, "w", Context::class.java, XC_MethodReplacement.returnConstant(2))
                XposedHelper.Companion.findAndHookMethod("com.meizu.net.lockscreenlibrary.manager.utilstool.baseutils.Utility", lpparam.classLoader, "isRoot", Context::class.java, XC_MethodReplacement.returnConstant(false))
                XposedHelper.Companion.findAndHookMethod("com.meizu.statsapp.v3.lib.plugin.f.b", lpparam.classLoader, "n", Context::class.java, XC_MethodReplacement.returnConstant(false))

                //resetToSystemTheme
                // findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.common.b", lpparam.classLoader, "c", XC_MethodReplacement.returnConstant(true));
                // 8.0.23
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.j.b", lpparam.classLoader, "e", XC_MethodReplacement.returnConstant(true))
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
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.k", lpparam.classLoader, "a", Context::class.java, String::class.java, Long::class.javaPrimitiveType, XC_MethodReplacement.returnConstant(null))
                /**
                 * public void k() {
                 * long v0 = SystemClock.elapsedRealtime() - this.n();
                 * com.meizu.customizecenter.manager.utilstool.c.b.e(this.b, "checkTrialFontWhenAppStart_interval:" + v0 / 1000 + " second");
                 * if(!this.f(v0)) {
                 * this.g();
                 * }
                 * }
                 */
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.g", lpparam.classLoader, "k", XC_MethodReplacement.returnConstant(null))
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.k", lpparam.classLoader, "k", XC_MethodReplacement.returnConstant(null))
                // 8.0.23
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.k", lpparam.classLoader, "b", XC_MethodReplacement.returnConstant(null))

                //"checkTrialFont:!isUsingTrialFont() Context context, String str, long j
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.k", lpparam.classLoader, "a", Context::class.java, String::class.java, Long::class.javaPrimitiveType, XC_MethodReplacement.returnConstant(null))
                val themeContentProvider: Class<*> = findClass("com.meizu.customizecenter.manager.utilshelper.dbhelper.dao.ThemeContentProvider", lpparam.classLoader)
                //主题混搭 ThemeContentProvider query Unknown URI
                XposedHelper.Companion.findAndHookMethod(themeContentProvider, "query", Uri::class.java, Array<String>::class.java, String::class.java, Array<String>::class.java, String::class.java, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val objs = param.args
                        val Tag = "(ITEMS LIKE"
                        val Tag2 = "%zklockscreen;%"
                        val Tag3 = "%com.meizu.flyme.weather;%"
                        var result = false
                        for (obj in objs) {
                            if (obj is String && (obj.contains(Tag) || obj == Tag2 || obj == Tag3)) {
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
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.utilshelper.restorehelper.ThemeRestoreService", lpparam.classLoader, "onStartCommand", Intent::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, XC_MethodReplacement.returnConstant(0))
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.utilshelper.restorehelper.FontRestoreService", lpparam.classLoader, "onStartCommand", Intent::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, XC_MethodReplacement.returnConstant(0))
                XposedHelper.Companion.findAndHookMethod("com.meizu.customizecenter.manager.utilshelper.scannerhelper.CustomizeScannerService", lpparam.classLoader, "onStartCommand", Intent::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, XC_MethodReplacement.returnConstant(0))
            }
        }
    }

    override fun initZygote(startupParam: StartupParam?) {}
}