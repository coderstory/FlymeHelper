package com.coderstory.flyme.module;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.SharedHelper;
import com.coderstory.flyme.utils.XposedHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class Others extends XposedHelper implements IModule {

    private static Context mContext = null;

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        if (resparam.packageName.equals("com.android.systemui")) {
            if (prefs.getBoolean("show_icon_battery_percentage", false)) {
                resparam.res.setReplacement(resparam.packageName, "string", "status_bar_settings_battery_meter_format_simple", "%d%%");
            }
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (loadPackageParam.packageName.equals("com.android.systemui")) {
            String className;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                className = "com.android.systemui.statusbar.phone.StatusBarIconControllerImpl";
            } else {
                className = "com.android.systemui.statusbar.phone.StatusBarIconController";
            }
            findAndHookMethod(className, loadPackageParam.classLoader, "setIconVisibility", String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                   // XposedBridge.log("图标类型: " + param.args[0].toString());
                    if ("alarm_clock".equals(param.args[0]) && prefs.getBoolean("hide_icon_alarm_clock", false)) {
                        param.args[1] = false;
                    }
                    if ("hotspot".equals(param.args[0]) && prefs.getBoolean("hide_icon_hotspot", false)) {
                        param.args[1] = false;
                    }
                    if ("bluetooth".equals(param.args[0]) && prefs.getBoolean("hide_icon_bluetooth", false)) {
                        param.args[1] = false;
                    }
                    if ("vpn".equals(param.args[0]) && prefs.getBoolean("hide_status_bar_vpn_icon", false)) {
                        param.args[1] = false;
                    }

                    if (("wifi".equals(param.args[0]) || "dual_wifi".equals(param.args[0])) && prefs.getBoolean("hide_status_bar_wifi_icon", false)) {
                        param.args[1] = false;
                    }
                    // 震动 || 静音+震动
                    if (("zen".equals(param.args[0]) || "volume".equals(param.args[0])) && prefs.getBoolean("hide_icon_shake", false)) {
                        param.args[1] = false;
                    }
                }
            });
            if (prefs.getBoolean("hide_icon_volte", false)) {

                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    hookAllMethods("com.android.systemui.statusbar.SignalClusterView", loadPackageParam.classLoader, "setMobileDataIndicators", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            param.args[4] = 0;
                        }
                    });
                }
                XposedBridge.log("SDK版本号: " + android.os.Build.VERSION.SDK_INT);
                // android 10
                if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    hookAllMethods("com.android.systemui.statusbar.policy.MobileSignalController", loadPackageParam.classLoader, "isVolteSwitchOn", XC_MethodReplacement.returnConstant(false));
                }
            }
            // com.android.systemui.power.PowerUI playBatterySound start 低电量 电量空
            if (prefs.getBoolean("hideDepWarn", false)) {
                hookAllMethods("com.flyme.systemui.developer.DeveloperSettingsController", loadPackageParam.classLoader, "updateDeveloperNotification", XC_MethodReplacement.returnConstant(null));
            }
            //隐藏 空sim卡图标
            if (prefs.getBoolean("hide_status_bar_no_sim_icon", false)) {
                findAndHookMethod("com.android.systemui.statusbar.policy.NetworkControllerImpl", loadPackageParam.classLoader, "updateNoSims", XC_MethodReplacement.returnConstant(null));
            }

            if (prefs.getBoolean("hide_status_bar_slow_rate_icon", false)) {
                hookAllMethods("com.flyme.systemui.statusbar.ConnectionRateView", loadPackageParam.classLoader, "updateConnectionRate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // 当前网速 单位kb
                        double rate = (double) param.args[0];
                        ImageView view = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mUnitView");
                        view.setVisibility(rate < 100 ? View.GONE : View.VISIBLE);
                    }
                });
            }

            XposedBridge.log("外部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
            hookAllMethods("com.android.systemui.statusbar.policy.Clock", loadPackageParam.classLoader, "getSmallTime", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("内部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
                    TextView view = (TextView) param.thisObject;
                    boolean is24HourFormat = DateFormat.is24HourFormat(view.getContext());
                    // HH:mm:ss EE 星期
                    String formatStr = is24HourFormat ? "HH:mm" : "hh:mm";
                    if (prefs.getBoolean("show_status_bar_time_second_icon", false)) {
                        formatStr += ":ss";
                    }
                    if (prefs.getBoolean("hide_status_bar_time_week_icon", false)) {
                        formatStr += " EE";
                    }
                    if (prefs.getBoolean("hide_status_bar_time_chinese_icon", false)) {
                        formatStr = getTimeType() + " " + formatStr;
                    }
                    XposedBridge.log("时间格式" + formatStr);
                    String time = new SimpleDateFormat(formatStr, Locale.getDefault(Locale.Category.FORMAT)).format(System.currentTimeMillis());
                    param.setResult(time);
                }
            });

            XposedBridge.log("开启隐藏热点图标" + prefs.getBoolean("hide_icon_hotspot", false));
            if (prefs.getBoolean("hide_icon_hotspot", false)) {

                findAndHookMethod("com.android.systemui.statusbar.policy.HotspotControllerImpl", loadPackageParam.classLoader, "setHotspotEnabled", boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.args[0] = false;
                    }
                });
                findAndHookMethod("com.android.systemui.statusbar.policy.HotspotControllerImpl", loadPackageParam.classLoader, "enableHotspot", boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.args[0] = false;
                    }
                });
            }

            if (prefs.getBoolean("hide_icon_bluetooth", false)) {
                findAndHookMethod("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchActiveDeviceChanged", XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchAudioModeChanged", XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchConnectionStateChanged", XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchDeviceAdded", XC_MethodReplacement.returnConstant(null));
            }
        }

        // 禁止安装app时候的安全检验
        if (loadPackageParam.packageName.equals("com.android.packageinstaller")) {

            if (prefs.getBoolean("enableCheckInstaller", false)) {
                // 8.x
                Class clazz = findClass("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader);
                if (clazz != null) {
                    findAndHookMethod(clazz, "setVirusCheckTime", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            Object mHandler = XposedHelpers.getObjectField(param.thisObject, "mHandler");
                            XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 5);
                            return null;
                        }
                    });
                    findAndHookMethod("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader, "replaceOrInstall", String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedHelpers.setObjectField(param.thisObject, "mAppInfo", null);
                        }
                    });
                }
            }
            if (prefs.getBoolean("enableCTS", false)) {
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
                    if (param.args[0] instanceof Context) {
                        mContext = (Context) param.args[0];
                    }
                }
            });
            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.d.a", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.args[0] instanceof Context) {
                        mContext = (Context) param.args[0];
                    }
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

    public String getTimeType() {
        String type = "";
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 0 && a < 6) {
            type = "深夜";
        }
        if (a >= 6 && a < 11) {
            type = "上午";
        }
        if (a >= 11 && a < 13) {
            type = "中午";
        }
        if (a >= 13 && a < 17) {
            type = "下午";
        }
        if (a >= 17 && a < 19) {
            type = "下午";
        }
        if (a >= 19 && a <= 24) {
            type = "晚上";
        }
        return type;
    }


    private boolean handleInfo(Object info) {
        boolean needToast = false;
        if (info != null) {
            String update = new SharedHelper(mContext).getString("updateList", "");
            // update = new String(android.util.Base64.decode(update, Base64.DEFAULT));
            String systemVersion = (String) XposedHelpers.getObjectField(info, "systemVersion");
            String updateUrl = (String) XposedHelpers.getObjectField(info, "updateUrl");
            String releaseDate = (String) XposedHelpers.getObjectField(info, "releaseDate");
            String fileSize = (String) XposedHelpers.getObjectField(info, "fileSize");
            String msg = systemVersion + "@" + updateUrl + "@" + fileSize + "@" + releaseDate;
            if (!update.contains(msg)) {
                needToast = true;
                update += msg + ";";
                if (mContext != null) {
                    XposedBridge.log("参数保存结果" + new SharedHelper(mContext).put("updateList", android.util.Base64.encodeToString(update.getBytes(), Base64.DEFAULT)));
                    Toast.makeText(mContext, "flyme助手:已检测到新的更新包地址", Toast.LENGTH_LONG).show();
                    XposedBridge.log("flyme助手: 检测完到更新包");
                    XposedBridge.log(update);
                } else {
                    XposedBridge.log("获取Context失败0x0");
                }
            } else {
                XposedBridge.log("flyme助手: 检测完到更新包已被记录");
            }
        }
        return needToast;
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

}
