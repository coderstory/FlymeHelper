package cn.coderstory.flyme.hook

import android.app.AndroidAppHelper
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.format.DateFormat
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedHelpers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SystemUIHooker : YukiBaseHooker() {
    override fun onHook() {
        // 状态栏 添加电量百分号
        resources().hook {
            injectResource {
                conditions {
                    name = "status_bar_settings_battery_meter_format_simple"
                    string()
                }
                replaceTo("%d%%")
            }
        }

        // 锁屏 运营商名称自定义
        "com.android.keyguard.CarrierTextManager\$CarrierTextCallbackInfo".hook {
            injectMember {
                method {
                    constructor()
                }
                beforeHook {
                    this.args[0] = "不忘初心 方得始终"
                }
            }
        }

        // 禁止充电动画
        "com.flyme.keyguard.charging.ChargeAnimationController".hook {
            injectMember {
                method {
                    name = "loadCharingView"
                }
                intercept()
            }

            injectMember {
                method {
                    constructor()
                }
                afterHook {
                    XposedHelpers.setObjectField(
                        this.instance,
                        "mStartAnimation",
                        Runnable {})
                }
            }

            injectMember {
                method {
                    name = "updateBatteryState"
                }
                intercept()
            }

        }
        // 侧滑返回 开启震动

        "com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler\$5".hook {
            injectMember {
                method {
                    name = "triggerBack"
                }
                beforeHook {
                    val vb = AndroidAppHelper.currentApplication().applicationContext.getSystemService(
                        Service.VIBRATOR_MANAGER_SERVICE
                    ) as VibratorManager
                    val vibrator: Vibrator = vb.getVibrator(0)
                    // 定义震动效果
                    val effect =
                        VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_CLICK)
                    // 开始震动
                    vibrator.vibrate(effect)
                }
            }
        }

        "com.android.systemui.statusbar.StatusBarIconView".hook {
            injectMember {
                method {
                    name = "set"
                }

                beforeHook {
                    val statusBarIcon = this.args[0]
                    if (XposedHelpers.getObjectField(statusBarIcon, "contentDescription") != null) {
                        val desc = XposedHelpers.getObjectField(statusBarIcon, "contentDescription").toString()
                        if (desc.contains("便携式热点") && prefs.getBoolean("hide_icon_hotspot", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                        } else if (desc.contains("USB") && prefs.getBoolean("hide_icon_debug", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                        } else if (desc.contains("流量") && prefs.getBoolean("hide_icon_save", false)) {
                            XposedHelpers.setBooleanField(statusBarIcon, "visible", false)
                        }
                    }
                }
            }

            injectMember {
                method { name = "setVisibility" }
                beforeHook {
                    val mIcon = XposedHelpers.getObjectField(instance, "mIcon")
                    if (mIcon != null) {
                        val contentDescription = XposedHelpers.getObjectField(mIcon, "contentDescription")
                        if (contentDescription != null && contentDescription.toString().endsWith("已激活VPN")) {
                            args[0] = View.GONE
                        }
                    }
                }
            }

            injectMember {
                method { name = "setVisibility" }
                beforeHook {
                    args[0] = View.GONE
                }
            }


        }

        "com.android.systemui.statusbar.phone.StatusBarIconController".hook {
            injectMember {
                method {
                    name = "setIconVisibility"
                    param(
                        String::class.java,
                        Boolean::class.java
                    )
                }
                beforeHook {
                    if ("rotate" == this.args[0]) {
                        this.args[1] = false
                    }
                }
            }
        }

        "com.android.systemui.statusbar.SignalClusterView".hook {
            injectMember {
                method {
                    name = "setMobileDataIndicators"
                }
                beforeHook {
                    args[4] = 0
                }
            }
        }

        "com.flyme.developer.DeveloperSettingsController".hook {
            injectMember {
                method {
                    name = "updateDeveloperNotification"
                }
                intercept()
            }
        }

        "com.android.systemui.statusbar.connectivity.NetworkControllerImpl".hook {
            injectMember {
                method {
                    name = "updateNoSims"
                }
                intercept()
            }
        }



        "com.android.flyme.statusbar.connectionRateView.ConnectionRateView".hook {
            injectMember {
                method {
                    name = "updateConnectionRate"
                }
                afterHook {
                    // 当前网速 单位kb
                    val rate = args[0] as Double
                    val view = XposedHelpers.getObjectField(
                        instance,
                        "mUnitView"
                    ) as View
                    view.visibility = if (rate < 100) View.GONE else View.VISIBLE
                }
            }
        }

        "com.android.systemui.statusbar.policy.Clock".hook {
            injectMember {
                method {
                    name = "getSmallTime"
                }
                afterHook {
                    if ("" != prefs.getString("status_bar_custom_time", "")) {
                        result = SimpleDateFormat(
                            prefs.getString("status_bar_custom_time", ""),
                            Locale.ENGLISH
                        ).format(Date())

                        //XposedBridge.log(param.result.toString())
                    } else {
                        val view = instance as TextView
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
                        result = time
                    }
                }
            }
        }

        "com.android.systemui.statusbar.policy.HotspotControllerImpl".hook {
            injectMember {
                method {
                    name {
                        it in listOf("setHotspotEnabled", "enableHotspot")
                    }
                }.all()
                beforeHook { args[0] = false }
            }


        }

        "com.android.settingslib.bluetooth.BluetoothEventManager".hook {
            injectMember {
                method {
                    name {
                        it in listOf("dispatchActiveDeviceChanged", "dispatchConnectionStateChanged")
                    }
                }.all()
                intercept()
            }
        }

        "com.android.systemui.statusbar.phone.StatusBarSignalPolicy".hook {
            injectMember {
                method { name = "setMobileDataIndicators" }
                afterHook {
                    var obj = args[0]
                    var subId = XposedHelpers.getIntField(obj, "subId")

                    var iconState =
                        XposedHelpers.callMethod(
                            instance,
                            "getState",
                            subId
                        )

                    if (iconState != null) {
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
            }
        }

        "com.android.systemui.statusbar.phone.PhoneStatusBarView".hook {
            injectMember {
                method { name = "onFinishInflate" }
                var preTime = 0L
                afterHook {
                    val statusVarView = instance as ViewGroup
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
            }
        }

        "com.flyme.systemui.statusbar.phone.StatusBarHeaderView".hook {
            injectMember {
                method { name = "onFinishInflate" }

                afterHook {
                    if (prefs.getBoolean("clock",false)) {
                        val timeView = XposedHelpers.getObjectField(instance,"mTime") as? ViewGroup
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

                    if (prefs.getBoolean("calendar",false)) {
                        val dateViewGroup = XposedHelpers.getObjectField(instance,"mDateGroup") as? ViewGroup
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

            }
        }


    }

    private val timeType: String
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