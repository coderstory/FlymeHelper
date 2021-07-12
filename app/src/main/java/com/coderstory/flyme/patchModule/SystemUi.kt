package com.coderstory.flyme.patchModule;

import android.app.AndroidAppHelper;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coderstory.flyme.tools.XposedHelper;
import com.coderstory.flyme.xposed.IModule;

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


            String status_bar_custom_carrier_name = prefs.getString("status_bar_custom_carrier_name", "");
            if (!status_bar_custom_carrier_name.equals("")) {
                hookAllMethods("com.flyme.systemui.statusbar.ext.FlymeStatusBarPluginImpl$FlymeNetWorkName", loadPackageParam.classLoader, "mergeNetWorkNames", XC_MethodReplacement.returnConstant(status_bar_custom_carrier_name));
            }

            //coord: (0,198,28) | addr: Lcom/flyme/systemui/charge/ChargeAnimationController;->loadCharingView(Z)V | loc: ?
            if (prefs.getBoolean("disable_charge_animation", false)) {
                findAndHookMethod("com.flyme.systemui.charge.ChargeAnimationController", loadPackageParam.classLoader, "loadCharingView", boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[0] = false;
                    }
                });
                hookAllConstructors("com.flyme.systemui.charge.ChargeAnimationController", loadPackageParam.classLoader, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedHelpers.setObjectField(param.thisObject, "mStartAnimation", (Runnable) () -> {
                        });
                    }
                });
                hookAllMethods("com.flyme.systemui.charge.ChargeAnimationController", loadPackageParam.classLoader, "updateBatteryState", XC_MethodReplacement.returnConstant(null));
            }

            if (!prefs.getString("enable_back_vibrator_value", "").equals("")) {
                if (Build.VERSION.SDK_INT == 30) {
                    findAndHookMethod("com.android.systemui.statusbar.phone.EdgeBackGestureHandler$4", loadPackageParam.classLoader, "triggerBack", getNotifyBackAction());
                } else if (Build.VERSION.SDK_INT == 29) {
                    hookAllMethods("com.android.systemui.recents.OverviewProxyService", loadPackageParam.classLoader, "notifyBackAction", getNotifyBackAction());
                }
            }

            hookAllMethods("com.android.systemui.statusbar.StatusBarIconView", loadPackageParam.classLoader, "set", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object statusBarIcon = param.args[0];
                    if (XposedHelpers.getObjectField(statusBarIcon, "contentDescription") != null) {
                        String desc = XposedHelpers.getObjectField(statusBarIcon, "contentDescription").toString();
                        //XposedBridge.log(desc);
                        if (desc.contains("便携式热点") && prefs.getBoolean("hide_icon_hotspot", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false);
                        } else if (desc.contains("USB") && prefs.getBoolean("hide_icon_debug", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false);
                        } else if (desc.contains("流量") && prefs.getBoolean("hide_icon_save", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false);
                        }
                    }
                }
            });

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
                    if ("rotate".equals(param.args[0])) {
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
                //XposedBridge.log("SDK版本号: " + android.os.Build.VERSION.SDK_INT);
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
                    String time = new SimpleDateFormat(formatStr, Locale.SIMPLIFIED_CHINESE).format(System.currentTimeMillis());
                    if (prefs.getBoolean("show_status_bar_time_am_pm", false)) {
                        time = new SimpleDateFormat("a", Locale.ENGLISH).format(System.currentTimeMillis()) + " " + time;
                    }
                    param.setResult(time);
                }
            });

            //XposedBridge.log("开启隐藏热点图标" + prefs.getBoolean("hide_icon_hotspot", false));
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
                hookAllMethods("com.android.systemui.statusbar.StatusBarIconView", loadPackageParam.classLoader, "setVisibility", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object mIcon = XposedHelpers.getObjectField(param.thisObject, "mIcon");
                        if (mIcon != null) {
                            Object contentDescription = XposedHelpers.getObjectField(mIcon, "contentDescription");
                            if (contentDescription != null && contentDescription.toString().endsWith("已激活VPN")) {
                                param.args[0] = View.GONE;
                            }
                        }
                    }
                });
            }

            //隐藏app图标
            if (prefs.getBoolean("hide_status_bar_app_icon", false)) {
                hookAllMethods("com.android.systemui.statusbar.StatusBarIconView", loadPackageParam.classLoader, "setVisibility", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[0] = View.GONE;
                    }
                });
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
                    int slot = (int) param.args[11];
                    if (prefs.getBoolean("hide_status_bar_sim1_icon", false) && slot == 1) {
                        XposedHelpers.setBooleanField(param.args[0], "visible", false);
                    }
                    if (prefs.getBoolean("hide_status_bar_sim2_icon", false) && slot == 2) {
                        XposedHelpers.setBooleanField(param.args[0], "visible", false);
                    }
                }
            });

            if (prefs.getString("isCore", "0").equals("1")) {
                hookAllConstructors("com.android.systemui.statusbar.phone.StatusBarSignalPolicy", loadPackageParam.classLoader, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return null;
                    }
                });
            }
        }

    }

    @NonNull
    private XC_MethodHook getNotifyBackAction() {
        return new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("notifyBackAction");
                XposedBridge.log("ggg" + param.thisObject.getClass().getName());
                Vibrator vb = (Vibrator) AndroidAppHelper.currentApplication().getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
                vb.vibrate(Long.parseLong(prefs.getString("enable_back_vibrator_value", "30")));
            }
        };
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
            type = "傍晚";
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
