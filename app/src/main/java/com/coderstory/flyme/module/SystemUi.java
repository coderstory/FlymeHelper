package com.coderstory.flyme.module;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coderstory.flyme.plugins.IModule;
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

public class SystemUi extends XposedHelper implements IModule {

    private static final TextView[] mClock = new TextView[1];


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

            //XposedBridge.log("外部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
            hookAllMethods("com.android.systemui.statusbar.policy.Clock", loadPackageParam.classLoader, "getSmallTime", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    // XposedBridge.log("内部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
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
                    // XposedBridge.log("时间格式" + formatStr);
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
                hookAllMethods("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchActiveDeviceChanged", XC_MethodReplacement.returnConstant(null));
                hookAllMethods("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchConnectionStateChanged", XC_MethodReplacement.returnConstant(null));
            }

            //隐藏 vpn图标
            if (prefs.getBoolean("hide_status_bar_vpn_icon", false)) {
                findAndHookMethod("com.flyme.systemui.statusbar.policy.VpnControllerImpl", loadPackageParam.classLoader, "setVpnEnabled", boolean.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.flyme.systemui.statusbar.policy.VpnControllerImpl", loadPackageParam.classLoader, "isVpnConnecting", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.flyme.systemui.statusbar.policy.VpnControllerImpl", loadPackageParam.classLoader, "isVpnEnabled", XC_MethodReplacement.returnConstant(false));
                hookAllMethods("com.flyme.systemui.statusbar.policy.VpnControllerImpl", loadPackageParam.classLoader, "notifyChanged", XC_MethodReplacement.returnConstant(null));
            }

            //时间居中
            if (prefs.getBoolean("status_text_view_clock_center", false)) {
                findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBarView", loadPackageParam.classLoader, "setBar", "com.android.systemui.statusbar.phone.StatusBar",
                        new XC_MethodHook() {
                            @Override
                            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                                ViewGroup phoneStatusBarView = (ViewGroup) param.thisObject;
                                final Context context = phoneStatusBarView.getContext();
                                Resources res = context.getResources();
                                TextView clock = phoneStatusBarView.findViewById(
                                        res.getIdentifier("clock", "id", "com.android.systemui"));
                                mClock[0] = clock;

                                ((ViewGroup) clock.getParent()).removeView(clock);
                                LinearLayout mCenterLayout = new LinearLayout(context);
                                LinearLayout.LayoutParams lp =
                                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                mCenterLayout.setLayoutParams(lp);
                                mCenterLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                                phoneStatusBarView.addView(mCenterLayout);
                                clock.setGravity(Gravity.BOTTOM);
                                clock.setPaddingRelative(0, 0, 0, 15);
                                mCenterLayout.addView(clock);
                            }
                        });

                hookAllMethods("com.flyme.systemui.statusbar.phone.FlymeMarqueeTicker", loadPackageParam.classLoader, "tickerDone", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (mClock[0] != null) {
                            mClock[0].setVisibility(View.VISIBLE);
                        }
                    }
                });

                hookAllMethods("com.flyme.systemui.statusbar.phone.MarqueeTextView", loadPackageParam.classLoader, "setText", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (mClock[0] != null && param.args[0] != null) {
                            mClock[0].setVisibility(View.INVISIBLE);
                        }
                    }
                });

                hookAllMethods("com.flyme.systemui.statusbar.phone.FlymeMarqueeTicker", loadPackageParam.classLoader, "tickerHalting", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (mClock[0] != null) {
                            mClock[0].setVisibility(View.VISIBLE);
                        }
                    }
                });

                hookAllMethods("com.flyme.systemui.statusbar.phone.FlymeMarqueeTicker", loadPackageParam.classLoader, "tickerStarting", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (mClock[0] != null) {
                            mClock[0].setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
            //coord: (200179,10,46) | addr: Lcom/android/systemui/statusbar/phone/StatusBarSignalPolicy$WifiIconState;->toString()Ljava/lang/String;+28h | loc: ?
            hookAllMethods("com.android.systemui.statusbar.phone.StatusBarSignalPolicy", loadPackageParam.classLoader, "setMobileDataIndicators", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (prefs.getBoolean("hide_status_bar_sim1_icon", false)) {
                        XposedHelpers.setBooleanField(param.args[0], "visible", false);
                    }
                    if (prefs.getBoolean("hide_status_bar_sim2_icon", false)) {
                        XposedHelpers.setBooleanField(param.args[1], "visible", false);
                    }
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

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

    }
}
