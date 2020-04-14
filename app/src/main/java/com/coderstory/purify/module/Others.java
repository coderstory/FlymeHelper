package com.coderstory.purify.module;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.widget.Toast;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.SharedHelper;
import com.coderstory.purify.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;




public class Others extends XposedHelper implements IModule {
    private SharedHelper helper = new SharedHelper(AndroidAppHelper.currentApplication().getApplicationContext());
    private static Context mContext = null;

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (loadPackageParam.packageName.equals("com.android.systemui")) {
            findAndHookMethod("com.android.systemui.statusbar.phone.StatusBarIconController", loadPackageParam.classLoader, "setIconVisibility", String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    if ("alarm_clock".equals(param.args[0]) && helper.getBoolean("hide_icon_alarm_clock", false)) {
                        param.args[1] = false;
                    }
                    if ("hotspot".equals(param.args[0]) && helper.getBoolean("hide_icon_hotspot", false)) {
                        param.args[1] = false;
                    }
                    if ("bluetooth".equals(param.args[0]) && helper.getBoolean("hide_icon_bluetooth", false)) {
                        param.args[1] = false;
                    }

                    XposedBridge.log("图标类型" + param.args[0].toString());
                    // 震动 || 静音+震动
                    if (("zen".equals(param.args[0]) || "volume".equals(param.args[0])) && helper.getBoolean("hide_icon_shake", false)) {
                        param.args[1] = false;
                    }
                }
            });
            if (helper.getBoolean("hide_icon_volte", false)) {
                hookAllMethods("com.android.systemui.statusbar.SignalClusterView", loadPackageParam.classLoader, "setMobileDataIndicators", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[4] = 0;
                    }
                });
            }
            // com.android.systemui.power.PowerUI playBatterySound start 低电量 电量空
            if (helper.getBoolean("hideDepWarn", false)) {
                hookAllMethods("com.flyme.systemui.developer.DeveloperSettingsController", loadPackageParam.classLoader, "updateDeveloperNotification", XC_MethodReplacement.returnConstant(null));
            }
            //隐藏 空sim卡图标
            if (helper.getBoolean("hide_status_bar_no_sim_icon", false)) {
                findAndHookMethod("com.android.systemui.statusbar.policy.NetworkControllerImpl", loadPackageParam.classLoader, "updateNoSims", XC_MethodReplacement.returnConstant(null));
            }

            //隐藏sim卡图标
            //findAndHookMethod("com.android.systemui.statusbar.policy.NetworkControllerImpl", loadPackageParam.classLoader, "notifySubscriptionsChangeCallBack", XC_MethodReplacement.returnConstant(null));

        }

        // 禁止安装app时候的安全检验
        if (loadPackageParam.packageName.equals("com.android.packageinstaller")) {

            if (helper.getBoolean("enableCheckInstaller", false)) {
                // 8.x
                Class clazz = findClass("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader);
                if (clazz == null) {
                    // 7.x
                    clazz = findClass("com.android.packageinstaller.PackageInstallerActivity", loadPackageParam.classLoader);
                }
                if (clazz != null) {
                    findAndHookMethod(clazz, "setVirusCheckTime", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            Object mHandler = XposedHelpers.getObjectField(param.thisObject, "mHandler");
                            XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 5);
                            return null;
                        }
                    });
                }
            }
            if (helper.getBoolean("enableCTS", false)) {
                XposedBridge.log("开启原生安装器");
                findAndHookMethod("com.meizu.safe.security.utils.Utils", loadPackageParam.classLoader, "isCtsRunning", XC_MethodReplacement.returnConstant(true));
            }
        }

        if (loadPackageParam.packageName.equals("com.meizu.flyme.update")) {

            // 获取Context
            // public abstract class a<T> implements ErrorListener, Listener {
            //     public a(Context context) {
            //        this.b = context.getApplicationContext();
            //        this.c = RequestManager.getInstance(this.b);
            //    }
            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.c.a", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = (Context) param.args[0];
                }
            });

            // 解析当前系统版本 待更新版本的zip包地址
            //       public class k {
            //                public b cdnCheckResult;
            //                public e currentFimware;
            //                public g firmwarePlan;
            //                public UpgradeFirmware upgradeFirmware;
            //
            //                public k(UpgradeFirmware upgradeFirmware, e eVar, g gVar, b bVar) {
            //                    this.upgradeFirmware = upgradeFirmware;
            //                    this.currentFimware = eVar;
            //                    this.firmwarePlan = gVar;
            //                    this.cdnCheckResult = bVar;
            //                }
            //            }

            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.model.n", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object obj = param.thisObject;

                    Object currentFimware = XposedHelpers.getObjectField(obj, "currentFimware");
                    handleInfo(currentFimware);

                    Object upgradeFirmware = XposedHelpers.getObjectField(obj, "upgradeFirmware");
                    handleInfo(upgradeFirmware);
                }
            });
        }

    }

    boolean handleInfo(Object info) {
        boolean needToast = false;
        if (info != null) {
            String update = helper.getString("updateList", "");
            String systemVersion = (String) XposedHelpers.getObjectField(info, "systemVersion");
            String updateUrl = (String) XposedHelpers.getObjectField(info, "updateUrl");
            String releaseDate = (String) XposedHelpers.getObjectField(info, "releaseDate");
            String fileSize = (String) XposedHelpers.getObjectField(info, "fileSize");
            String msg = systemVersion + "@" + updateUrl + "@" + fileSize + "@" + releaseDate;
            if (!update.contains(msg)) {
                needToast = true;
                update += msg + ";";
                helper.put("updateList", update);
                if (mContext != null) {
                    Toast.makeText(mContext, "flyme助手:已检测到新的更新包地址", Toast.LENGTH_LONG).show();
                }
            }
        }
        return needToast;
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

}
