package com.coderstory.purify.module;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.purify.config.Misc.isEnable;

public class ThemePatcher extends XposedHelper implements IModule {


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        if (!isEnable()) {
            return;
        }

        // 主题和谐
        if (lpparam.packageName.equals("com.meizu.customizecenter") && prefs.getBoolean("enabletheme", false)) {

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
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.g", lpparam.classLoader, "a", Boolean.class, Boolean.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.g", lpparam.classLoader, "k", XC_MethodReplacement.returnConstant(null));

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