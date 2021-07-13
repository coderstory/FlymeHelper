package com.coderstory.flyme.patchModule


import android.app.AndroidAppHelper
import android.app.Service
import android.os.Build
import android.os.Vibrator
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.text.SimpleDateFormat
import java.util.*

class SystemUi : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {
        if (respray.packageName == "com.android.systemui") {
            if (prefs.getBoolean("show_icon_battery_percentage", false)) {
                respray.res.setReplacement(respray.packageName, "string", "status_bar_settings_battery_meter_format_simple", "%d%%")
            }
        }
    }

    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (loadPackageParam.packageName == "com.android.systemui") {
            val status_bar_custom_carrier_name = prefs.getString("status_bar_custom_carrier_name", "")
            if (status_bar_custom_carrier_name != "") {
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.statusbar.ext.FlymeStatusBarPluginImpl\$FlymeNetWorkName", loadPackageParam.classLoader, "mergeNetWorkNames", XC_MethodReplacement.returnConstant(status_bar_custom_carrier_name))
            }

            //coord: (0,198,28) | addr: Lcom/flyme/systemui/charge/ChargeAnimationController;->loadCharingView(Z)V | loc: ?
            if (prefs.getBoolean("disable_charge_animation", false)) {
                XposedHelper.Companion.findAndHookMethod("com.flyme.systemui.charge.ChargeAnimationController", loadPackageParam.classLoader, "loadCharingView", Boolean::class.javaPrimitiveType, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.beforeHookedMethod(param)
                        param.args[0] = false
                    }
                })
                XposedHelper.Companion.hookAllConstructors("com.flyme.systemui.charge.ChargeAnimationController", loadPackageParam.classLoader, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        XposedHelpers.setObjectField(param.thisObject, "mStartAnimation", Runnable {})
                    }
                })
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.charge.ChargeAnimationController", loadPackageParam.classLoader, "updateBatteryState", XC_MethodReplacement.returnConstant(null))
            }
            if (prefs.getString("enable_back_vibrator_value", "") != "") {
                if (Build.VERSION.SDK_INT == 30) {
                    XposedHelper.Companion.findAndHookMethod("com.android.systemui.statusbar.phone.EdgeBackGestureHandler$4", loadPackageParam.classLoader, "triggerBack", notifyBackAction)
                } else if (Build.VERSION.SDK_INT == 29) {
                    XposedHelper.Companion.hookAllMethods("com.android.systemui.recents.OverviewProxyService", loadPackageParam.classLoader, "notifyBackAction", notifyBackAction)
                }
            }
            XposedHelper.Companion.hookAllMethods("com.android.systemui.statusbar.StatusBarIconView", loadPackageParam.classLoader, "set", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val statusBarIcon = param.args[0]
                    if (XposedHelpers.getObjectField(statusBarIcon, "contentDescription") != null) {
                        val desc = XposedHelpers.getObjectField(statusBarIcon, "contentDescription").toString()
                        //XposedBridge.log(desc);
                        if (desc.contains("便携式热点") && prefs.getBoolean("hide_icon_hotspot", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                        } else if (desc.contains("USB") && prefs.getBoolean("hide_icon_debug", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                        } else if (desc.contains("流量") && prefs.getBoolean("hide_icon_save", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                        }
                    }
                }
            })
            val className: String
            className = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "com.android.systemui.statusbar.phone.StatusBarIconControllerImpl"
            } else {
                "com.android.systemui.statusbar.phone.StatusBarIconController"
            }
            XposedHelper.Companion.findAndHookMethod(className, loadPackageParam.classLoader, "setIconVisibility", String::class.java, Boolean::class.javaPrimitiveType, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    if ("rotate" == param.args[0]) {
                        param.args[1] = false
                    }
                }
            })
            if (prefs.getBoolean("hide_icon_volte", false)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    XposedHelper.Companion.hookAllMethods("com.android.systemui.statusbar.SignalClusterView", loadPackageParam.classLoader, "setMobileDataIndicators", object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            super.beforeHookedMethod(param)
                            param.args[4] = 0
                        }
                    })
                }
                //XposedBridge.log("SDK版本号: " + android.os.Build.VERSION.SDK_INT);
                // android 10
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    XposedHelper.Companion.hookAllMethods("com.android.systemui.statusbar.policy.MobileSignalController", loadPackageParam.classLoader, "isVolteSwitchOn", XC_MethodReplacement.returnConstant(false))
                }
            }
            // com.android.systemui.power.PowerUI playBatterySound start 低电量 电量空
            if (prefs.getBoolean("hideDepWarn", false)) {
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.developer.DeveloperSettingsController", loadPackageParam.classLoader, "updateDeveloperNotification", XC_MethodReplacement.returnConstant(null))
            }
            //隐藏 空sim卡图标
            if (prefs.getBoolean("hide_status_bar_no_sim_icon", false)) {
                XposedHelper.Companion.findAndHookMethod("com.android.systemui.statusbar.policy.NetworkControllerImpl", loadPackageParam.classLoader, "updateNoSims", XC_MethodReplacement.returnConstant(null))
            }
            if (prefs.getBoolean("hide_status_bar_slow_rate_icon", false)) {
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.statusbar.ConnectionRateView", loadPackageParam.classLoader, "updateConnectionRate", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        // 当前网速 单位kb
                        val rate = param.args[0] as Double
                        val view = XposedHelpers.getObjectField(param.thisObject, "mUnitView") as ImageView
                        view.visibility = if (rate < 100) View.GONE else View.VISIBLE
                    }
                })
            }

            //XposedBridge.log("外部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
            XposedHelper.Companion.hookAllMethods("com.android.systemui.statusbar.policy.Clock", loadPackageParam.classLoader, "getSmallTime", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    // XposedBridge.log("内部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
                    val view = param.thisObject as TextView
                    val is24HourFormat = DateFormat.is24HourFormat(view.context)
                    // HH:mm:ss EE 星期
                    var formatStr = if (is24HourFormat) "HH:mm" else "hh:mm"
                    if (prefs.getBoolean("show_status_bar_time_second_icon", false)) {
                        formatStr += ":ss"
                    }
                    if (prefs.getBoolean("hide_status_bar_time_week_icon", false)) {
                        formatStr += " EE"
                    }
                    if (prefs.getBoolean("hide_status_bar_time_chinese_icon", false)) {
                        formatStr = "$timeType $formatStr"
                    }
                    // XposedBridge.log("时间格式" + formatStr);
                    var time = SimpleDateFormat(formatStr, Locale.SIMPLIFIED_CHINESE).format(System.currentTimeMillis())
                    if (prefs.getBoolean("show_status_bar_time_am_pm", false)) {
                        time = SimpleDateFormat("a", Locale.ENGLISH).format(System.currentTimeMillis()) + " " + time
                    }
                    param.result = time
                }
            })

            //XposedBridge.log("开启隐藏热点图标" + prefs.getBoolean("hide_icon_hotspot", false));
            if (prefs.getBoolean("hide_icon_hotspot", false)) {
                XposedHelper.Companion.findAndHookMethod("com.android.systemui.statusbar.policy.HotspotControllerImpl", loadPackageParam.classLoader, "setHotspotEnabled", Boolean::class.javaPrimitiveType, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.args[0] = false
                    }
                })
                XposedHelper.Companion.findAndHookMethod("com.android.systemui.statusbar.policy.HotspotControllerImpl", loadPackageParam.classLoader, "enableHotspot", Boolean::class.javaPrimitiveType, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.args[0] = false
                    }
                })
            }
            if (prefs.getBoolean("hide_icon_bluetooth", false)) {
                XposedHelper.Companion.hookAllMethods("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchActiveDeviceChanged", XC_MethodReplacement.returnConstant(null))
                XposedHelper.Companion.hookAllMethods("com.android.settingslib.bluetooth.BluetoothEventManager", loadPackageParam.classLoader, "dispatchConnectionStateChanged", XC_MethodReplacement.returnConstant(null))
            }

            //隐藏 vpn图标
            if (prefs.getBoolean("hide_status_bar_vpn_icon", false)) {
                XposedHelper.Companion.hookAllMethods("com.android.systemui.statusbar.StatusBarIconView", loadPackageParam.classLoader, "setVisibility", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.beforeHookedMethod(param)
                        val mIcon = XposedHelpers.getObjectField(param.thisObject, "mIcon")
                        if (mIcon != null) {
                            val contentDescription = XposedHelpers.getObjectField(mIcon, "contentDescription")
                            if (contentDescription != null && contentDescription.toString().endsWith("已激活VPN")) {
                                param.args[0] = View.GONE
                            }
                        }
                    }
                })
            }

            //隐藏app图标
            if (prefs.getBoolean("hide_status_bar_app_icon", false)) {
                XposedHelper.Companion.hookAllMethods("com.android.systemui.statusbar.StatusBarIconView", loadPackageParam.classLoader, "setVisibility", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.beforeHookedMethod(param)
                        param.args[0] = View.GONE
                    }
                })
            }

            //时间居中
            if (prefs.getBoolean("status_text_view_clock_center", false)) {
                XposedHelper.Companion.findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBarView", loadPackageParam.classLoader, "setBar", "com.android.systemui.statusbar.phone.StatusBar",
                        object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            public override fun afterHookedMethod(param: MethodHookParam) {
                                val phoneStatusBarView = param.thisObject as ViewGroup
                                val context = phoneStatusBarView.context
                                val res = context.resources
                                val clock = phoneStatusBarView.findViewById<TextView>(
                                        res.getIdentifier("clock", "id", "com.android.systemui"))
                                //SystemUi.Companion.mClock.get(0) = clock
                                mClock[0] = clock
                                (clock.parent as ViewGroup).removeView(clock)
                                val mCenterLayout = LinearLayout(context)
                                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                                mCenterLayout.layoutParams = lp
                                mCenterLayout.gravity = Gravity.CENTER_HORIZONTAL
                                phoneStatusBarView.addView(mCenterLayout)
                                clock.gravity = Gravity.BOTTOM
                                clock.setPaddingRelative(0, 0, 0, 15)
                                mCenterLayout.addView(clock)
                            }
                        })
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.statusbar.phone.FlymeMarqueeTicker", loadPackageParam.classLoader, "tickerDone", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        SystemUi.Companion.mClock.get(0)?.visibility = View.VISIBLE
                    }
                })
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.statusbar.phone.MarqueeTextView", loadPackageParam.classLoader, "setText", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        if (SystemUi.Companion.mClock.get(0) != null && param.args[0] != null) {
                            SystemUi.Companion.mClock.get(0)!!.visibility = View.INVISIBLE
                        }
                    }
                })
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.statusbar.phone.FlymeMarqueeTicker", loadPackageParam.classLoader, "tickerHalting", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)

                        SystemUi.Companion.mClock.get(0)?.visibility = View.VISIBLE

                    }
                })
                XposedHelper.Companion.hookAllMethods("com.flyme.systemui.statusbar.phone.FlymeMarqueeTicker", loadPackageParam.classLoader, "tickerStarting", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)

                        SystemUi.Companion.mClock.get(0)?.visibility = View.INVISIBLE

                    }
                })
            }
            //coord: (200179,10,46) | addr: Lcom/android/systemui/statusbar/phone/StatusBarSignalPolicy$WifiIconState;->toString()Ljava/lang/String;+28h | loc: ?
            XposedHelper.Companion.hookAllMethods("com.android.systemui.statusbar.phone.StatusBarSignalPolicy", loadPackageParam.classLoader, "setMobileDataIndicators", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    val slot = param.args[11] as Int
                    if (prefs.getBoolean("hide_status_bar_sim1_icon", false) && slot == 1) {
                        XposedHelpers.setBooleanField(param.args[0], "visible", false)
                    }
                    if (prefs.getBoolean("hide_status_bar_sim2_icon", false) && slot == 2) {
                        XposedHelpers.setBooleanField(param.args[0], "visible", false)
                    }
                }
            })
            if (prefs.getString("isCore", "0") == "1") {
                XposedHelper.Companion.hookAllConstructors("com.android.systemui.statusbar.phone.StatusBarSignalPolicy", loadPackageParam.classLoader, object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam) {
                    }
                })
            }
        }
    }

    private val notifyBackAction: XC_MethodHook
        private get() = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                XposedBridge.log("notifyBackAction")
                XposedBridge.log("ggg" + param.thisObject.javaClass.name)
                val vb = AndroidAppHelper.currentApplication().applicationContext.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                vb.vibrate(prefs.getString("enable_back_vibrator_value", "30")!!.toLong())
            }
        }
    val timeType: String
        get() {
            var type = ""
            val date = Date()
            val df = SimpleDateFormat("HH")
            val str = df.format(date)
            val a = str.toInt()
            if (a >= 0 && a < 6) {
                type = "深夜"
            }
            if (a >= 6 && a < 11) {
                type = "上午"
            }
            if (a >= 11 && a < 13) {
                type = "中午"
            }
            if (a >= 13 && a < 17) {
                type = "下午"
            }
            if (a >= 17 && a < 19) {
                type = "傍晚"
            }
            if (a >= 19 && a <= 24) {
                type = "晚上"
            }
            return type
        }

    override fun initZygote(startupParam: StartupParam?) {}

    companion object {
        private val mClock = arrayOfNulls<TextView>(1)
    }
}