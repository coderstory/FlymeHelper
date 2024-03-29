package com.coderstory.flyme10.patchModule


import android.app.AndroidAppHelper
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateFormat
import android.view.*
import android.widget.TextView
import com.coderstory.flyme10.tools.XposedHelper
import com.coderstory.flyme10.tools.getObjectField
import com.coderstory.flyme10.xposed.IModule
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
                respray.res.setReplacement(
                    respray.packageName,
                    "string",
                    "status_bar_settings_battery_meter_format_simple",
                    "%d%%"
                )
            }
        }
    }

    override fun handleLoadPackage(param: LoadPackageParam) {
        if (param.packageName == "com.android.systemui") {
            val statusBarCustomCarrierName = prefs.getString("status_bar_custom_carrier_name", "")
            if (statusBarCustomCarrierName != "") {
                hookAllConstructors(
                    findClass("com.android.keyguard.CarrierTextManager\$CarrierTextCallbackInfo",
                        param.classLoader),
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                           param.args[0] = statusBarCustomCarrierName
                        }
                    })
            }

            //coord: (0,198,28) | addr: Lcom/flyme/systemui/charge/ChargeAnimationController;->loadCharingView(Z)V | loc: ?
            if (prefs.getBoolean("disable_charge_animation", false)) {


                findAndHookMethod(
                    "com.flyme.keyguard.charging.ChargeAnimationController",
                    param.classLoader,
                    "loadCharingView",
                    XC_MethodReplacement.returnConstant(null)
                )
                hookAllConstructors(
                    "com.flyme.keyguard.charging.ChargeAnimationController",
                    param.classLoader,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            XposedHelpers.setObjectField(
                                param.thisObject,
                                "mStartAnimation",
                                Runnable {})
                        }
                    })
                hookAllMethods(
                    "com.flyme.keyguard.charging.ChargeAnimationController",
                    param.classLoader,
                    "updateBatteryState",
                    XC_MethodReplacement.returnConstant(null)
                )
            }
            if (prefs.getString("enable_back_vibrator_value", "") != "") {
                if (Build.VERSION.SDK_INT == 30) {
                    findAndHookMethod(
                        "com.android.systemui.statusbar.phone.EdgeBackGestureHandler$4",
                        param.classLoader,
                        "triggerBack",
                        notifyBackAction
                    )
                    findAndHookMethod(
                        "com.android.systemui.statusbar.phone.EdgeBackGestureHandler$5",
                        param.classLoader,
                        "triggerBack",
                        notifyBackAction
                    )
                } else if (Build.VERSION.SDK_INT == 29) {
                    hookAllMethods(
                        "com.android.systemui.recents.OverviewProxyService",
                        param.classLoader,
                        "notifyBackAction",
                        notifyBackAction
                    )
                } else if (Build.VERSION.SDK_INT == 33) {
                    hookAllMethods(
                        "com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler$5",
                        param.classLoader,
                        "triggerBack",
                        notifyBackAction
                    )
                }
            }



            hookAllMethods(
                "com.android.systemui.statusbar.StatusBarIconView",
                param.classLoader,
                "set",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        val statusBarIcon = param.args[0]
                        if (XposedHelpers.getObjectField(
                                statusBarIcon,
                                "contentDescription"
                            ) != null
                        ) {
                            val desc =
                                XposedHelpers.getObjectField(statusBarIcon, "contentDescription")
                                    .toString()
                            //XposedBridge.log(desc);
                            if (desc.contains("便携式热点") && prefs.getBoolean(
                                    "hide_icon_hotspot",
                                    false
                                )
                            ) {
                                XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                            } else if (desc.contains("USB") && prefs.getBoolean(
                                    "hide_icon_debug",
                                    false
                                )
                            ) {
                                XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                            } else if (desc.contains("流量") && prefs.getBoolean(
                                    "hide_icon_save",
                                    false
                                )
                            ) {
                                XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                            }
                        }
                    }
                })
            val className: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "com.android.systemui.statusbar.phone.StatusBarIconControllerImpl"
            } else {
                "com.android.systemui.statusbar.phone.StatusBarIconController"
            }
            findAndHookMethod(
                className,
                param.classLoader,
                "setIconVisibility",
                String::class.java,
                Boolean::class.javaPrimitiveType,
                object : XC_MethodHook() {
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
                    hookAllMethods(
                        "com.android.systemui.statusbar.SignalClusterView",
                        param.classLoader,
                        "setMobileDataIndicators",
                        object : XC_MethodHook() {
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
                    hookAllMethods(
                        "com.android.systemui.statusbar.policy.MobileSignalController",
                        param.classLoader,
                        "isVolteSwitchOn",
                        XC_MethodReplacement.returnConstant(false)
                    )
                }
            }
            // com.android.systemui.power.PowerUI playBatterySound start 低电量 电量空
            if (prefs.getBoolean("hideDepWarn", false)) {
                hookAllMethods(
                    "com.flyme.developer.DeveloperSettingsController",
                    param.classLoader,
                    "updateDeveloperNotification",
                    XC_MethodReplacement.returnConstant(null)
                )
            }

            //隐藏 空sim卡图标
            if (prefs.getBoolean("hide_status_bar_no_sim_icon", false)) {
                XposedBridge.log("开启隐藏空sim卡")
                findAndHookMethod(
                    "com.android.systemui.statusbar.connectivity.NetworkControllerImpl",
                    param.classLoader,
                    "updateNoSims",
                    XC_MethodReplacement.returnConstant(null)
                )
            }
            if (prefs.getBoolean("hide_status_bar_slow_rate_icon", false)) {
                hookAllMethods(
                    "com.android.flyme.statusbar.connectionRateView.ConnectionRateView",
                    param.classLoader,
                    "updateConnectionRate",
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            // 当前网速 单位kb
                            val rate = param.args[0] as Double
                            val view = XposedHelpers.getObjectField(
                                param.thisObject,
                                "mUnitView"
                            ) as View
                            view.visibility = if (rate < 100) View.GONE else View.VISIBLE
                        }
                    })
            }

            //XposedBridge.log("外部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
            hookAllMethods(
                "com.android.systemui.statusbar.policy.Clock",
                param.classLoader,
                "getSmallTime",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        // XposedBridge.log("内部读取值" + prefs.getBoolean("show_status_bar_time_second_icon", false));
                        if ("" != prefs.getString("status_bar_custom_time", "")) {
                            param.result = SimpleDateFormat(
                                prefs.getString("status_bar_custom_time", ""),
                                Locale.ENGLISH
                            ).format(Date())

                            //XposedBridge.log(param.result.toString())
                        } else {
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
                            var time = SimpleDateFormat(
                                formatStr,
                                (if (prefs.getBoolean(
                                        "hide_status_bar_time_eng_icon",
                                        false
                                    )
                                ) Locale.ENGLISH else Locale.SIMPLIFIED_CHINESE)
                            ).format(System.currentTimeMillis())
                            if (prefs.getBoolean("show_status_bar_time_am_pm", false)) {
                                time = SimpleDateFormat(
                                    "a",
                                    Locale.ENGLISH
                                ).format(System.currentTimeMillis()) + " " + time
                            }
                            param.result = time
                        }

                    }
                })

            //XposedBridge.log("开启隐藏热点图标" + prefs.getBoolean("hide_icon_hotspot", false));
            if (prefs.getBoolean("hide_icon_hotspot", false)) {
                findAndHookMethod(
                    "com.android.systemui.statusbar.policy.HotspotControllerImpl",
                    param.classLoader,
                    "setHotspotEnabled",
                    Boolean::class.javaPrimitiveType,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.args[0] = false
                        }
                    })
                findAndHookMethod(
                    "com.android.systemui.statusbar.policy.HotspotControllerImpl",
                    param.classLoader,
                    "enableHotspot",
                    Boolean::class.javaPrimitiveType,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.args[0] = false
                        }
                    })
            }
            if (prefs.getBoolean("hide_icon_bluetooth", false)) {
                hookAllMethods(
                    "com.android.settingslib.bluetooth.BluetoothEventManager",
                    param.classLoader,
                    "dispatchActiveDeviceChanged",
                    XC_MethodReplacement.returnConstant(null)
                )
                hookAllMethods(
                    "com.android.settingslib.bluetooth.BluetoothEventManager",
                    param.classLoader,
                    "dispatchConnectionStateChanged",
                    XC_MethodReplacement.returnConstant(null)
                )
            }

            //隐藏 vpn图标
            if (prefs.getBoolean("hide_status_bar_vpn_icon", false)) {
                hookAllMethods(
                    "com.android.systemui.statusbar.StatusBarIconView",
                    param.classLoader,
                    "setVisibility",
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            super.beforeHookedMethod(param)
                            val mIcon = XposedHelpers.getObjectField(param.thisObject, "mIcon")
                            if (mIcon != null) {
                                val contentDescription =
                                    XposedHelpers.getObjectField(mIcon, "contentDescription")
                                if (contentDescription != null && contentDescription.toString()
                                        .endsWith("已激活VPN")
                                ) {
                                    param.args[0] = View.GONE
                                }
                            }
                        }
                    })
            }

            //隐藏app图标
            if (prefs.getBoolean("hide_status_bar_app_icon", false)) {
                hookAllMethods(
                    "com.android.systemui.statusbar.StatusBarIconView",
                    param.classLoader,
                    "setVisibility",
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            super.beforeHookedMethod(param)
                            param.args[0] = View.GONE
                        }
                    })
            }

            //coord: (200179,10,46) | addr: Lcom/android/systemui/statusbar/phone/StatusBarSignalPolicy$WifiIconState;->toString()Ljava/lang/String;+28h | loc: ?
            hookAllMethods(
                "com.android.systemui.statusbar.phone.StatusBarSignalPolicy",
                param.classLoader,
                "setMobileDataIndicators",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        // public void setMobileDataIndicators(MobileDataIndicators indicators) {
                        //        Log.d(TAG, "setMobileDataIndicators: " + indicators);
                        //        MobileIconState state = getState(indicators.subId);
                        //        if (state == null) {
                        //            return;
                        //        }
                        //        boolean z = true;
                        //        boolean typeChanged = indicators.statusType != state.typeId && (indicators.statusType == 0 || state.typeId == 0);
                        //        state.visible = indicators.statusIcon.visible && !this.mHideMobile;
                        var obj = param.args[0]
                        var subId = XposedHelpers.getIntField(obj, "subId")

                        var iconState =
                            XposedHelpers.callMethod(
                                param.thisObject,
                                "getState",
                                subId
                            )

                       if(iconState!=null){
                           val slotId = XposedHelpers.getIntField(iconState, "subId") + 1

                           // XposedBridge.log("当前卡槽$slotId")
                           if (prefs.getBoolean("hide_status_bar_sim1_icon", false) && slotId == 1) {
                               // XposedBridge.log("开启隐藏sim1")
                               XposedHelpers.setBooleanField(iconState, "visible", false)
                           }
                           if (prefs.getBoolean("hide_status_bar_sim2_icon", false) && slotId == 2) {
                               // XposedBridge.log("开启隐藏sim2")
                               XposedHelpers.setBooleanField(iconState, "visible", false)
                           }
                       }

                    }
                })

            //双击状态栏锁屏
            if (prefs.getBoolean("double_clock_sleep", true)) {
                findAndHookMethod(
                    "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                    param.classLoader,
                    "onFinishInflate",
                    object : XC_MethodHook() {
                        var preTime = 0L
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val statusVarView = param.thisObject as ViewGroup
                            statusVarView.setOnTouchListener { view, event ->
                                if (event.action == MotionEvent.ACTION_DOWN) {
//                                Log.d("LSPosed","点击啦状态栏")
                                    val currTime = System.currentTimeMillis()
                                    if (currTime - preTime <= 200) {
                                        XposedHelpers.callMethod(
                                            view.context.getSystemService(Context.POWER_SERVICE),
                                            "goToSleep",
                                            SystemClock.uptimeMillis()
                                        )
                                    }
                                    preTime = currTime
                                }
                                view.performClick()
                                return@setOnTouchListener false
                            }
                        }
                    })
            }

            val clickClock = prefs.getBoolean("click_to_clock", true)
            val clickCalendar = prefs.getBoolean("click_to_calendar", true)
            if (clickClock || clickCalendar) {
                //点击下拉通知栏的时间进入时钟/日历
                findAndHookMethod(
                    "com.flyme.systemui.statusbar.phone.StatusBarHeaderView",
                    param.classLoader,
                    "onFinishInflate",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(paramThis: MethodHookParam) {


                            if (clickClock) {
                                val timeView = paramThis.thisObject.getObjectField("mTime") as? View
                                timeView?.setOnClickListener {
                                    //跳转系统闹钟
                                    it.context.startActivity(Intent().apply {
                                        setClassName(
                                            "com.android.alarmclock",
                                            "com.meizu.flyme.alarmclock.DeskClock"
                                        )
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })

                                }
                            }

                            if (clickCalendar) {
                                val dateViewGroup =
                                    paramThis.thisObject.getObjectField("mDateGroup") as? ViewGroup
                                dateViewGroup?.setOnClickListener {
                                    //跳转系统闹钟
                                    it.context.startActivity(Intent().apply {
                                        setClassName(
                                            "com.android.calendar",
                                            "com.meizu.flyme.calendar.AllInOneActivity"
                                        )
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                }
                            }
                        }
                    })
            }
        }
    }

    override fun initZygote(startupParam: StartupParam?) {

    }

    private val notifyBackAction: XC_MethodHook
        get() = object : XC_MethodHook() {
            val duration = prefs.getString("enable_back_vibrator_value", "30")!!.toLong()

            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)

                val vb = AndroidAppHelper.currentApplication().applicationContext.getSystemService(
                    Service.VIBRATOR_SERVICE
                ) as Vibrator

                vb.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }
    val timeType: String
        get() {
            var type = ""
            val date = Date()
            val df = SimpleDateFormat("HH", Locale.CHINA)
            val str = df.format(date)
            val a = str.toInt()
            if (a in 0..4) {
                type = "深夜"
            }
            if (a in 5..8) {
                type = "清晨"
            }
            if (a in 9..10) {
                type = "上午"
            }
            if (a in 11..12) {
                type = "中午"
            }
            if (a in 13..16) {
                type = "下午"
            }
            if (a in 17..18) {
                type = "傍晚"
            }
            if (a in 19..24) {
                type = "晚上"
            }
            return type
        }
}
