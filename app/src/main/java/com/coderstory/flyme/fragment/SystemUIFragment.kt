package com.coderstory.flyme.fragment


import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment
import com.coderstory.flyme.tools.SharedHelper
import com.coderstory.flyme.tools.Utils
import com.topjohnwu.superuser.Shell
import java.util.*
import java.util.stream.Collectors

class SystemUIFragment : BaseFragment() {
    override fun setUpView() {
        `$`<View>(R.id.hide_icon_alarm_clock).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_alarm_clock", (v as SwitchCompat).isChecked)
            fix()
            updateIcon()
        }
        `$`<View>(R.id.hide_icon_bluetooth).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_bluetooth", (v as SwitchCompat).isChecked)
            fix()
            updateIcon()
        }
        `$`<View>(R.id.hide_icon_hotspot).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_hotspot", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_icon_save).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_save", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_icon_debug).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_debug", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_icon_volte).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_volte", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_icon_shake).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_shake", (v as SwitchCompat).isChecked)
            fix()
            updateIcon()
        }
        `$`<View>(R.id.hide_status_bar_no_sim_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_no_sim_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_status_bar_wifi_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_wifi_icon", (v as SwitchCompat).isChecked)
            fix()
            updateIcon()
        }
        `$`<View>(R.id.hide_status_bar_vpn_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_vpn_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.show_icon_battery_percentage).setOnClickListener { v: View ->
            editor.putBoolean("show_icon_battery_percentage", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.show_status_bar_time_second_icon).setOnClickListener { v: View ->
            editor.putBoolean("show_status_bar_time_second_icon", (v as SwitchCompat).isChecked)
            fix()
            Shell.su("settings put secure clock_seconds " + if (v.isChecked) "1" else "0").exec()
        }
        `$`<View>(R.id.hide_status_bar_slow_rate_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_slow_rate_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_status_bar_time_week_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_time_week_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_status_bar_time_chinese_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_time_chinese_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.status_text_view_clock_center).setOnClickListener { v: View ->
            editor.putBoolean("status_text_view_clock_center", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_status_bar_sim1_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_sim1_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_status_bar_sim2_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_sim2_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_status_bar_location_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_location_icon", (v as SwitchCompat).isChecked)
            fix()
            updateIcon()
        }
        `$`<View>(R.id.hide_status_bar_clock_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_clock_icon", (v as SwitchCompat).isChecked)
            fix()
            updateIcon()
        }
        `$`<View>(R.id.hide_status_bar_battery_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_battery_icon", (v as SwitchCompat).isChecked)
            fix()
            updateIcon()
        }
        `$`<View>(R.id.hide_status_bar_app_icon).setOnClickListener { v: View ->
            editor.putBoolean("hide_status_bar_app_icon", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.show_status_bar_time_am_pm).setOnClickListener { v: View ->
            editor.putBoolean("show_status_bar_time_am_pm", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.status_text_view_lyric_center).setOnClickListener { v: View ->
            editor.putBoolean("status_text_view_lyric_center", (v as SwitchCompat).isChecked)
            fix()
        }

        val carrierName = `$`<EditText>(R.id.status_bar_custom_carrier_name)
        carrierName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                editor.putString("status_bar_custom_carrier_name", editable.toString())
                fix()
            }
        })
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_system_ui
    }

    override fun setUpData() {
        (`$`<View>(R.id.status_bar_custom_carrier_name) as EditText).setText(prefs.getString("status_bar_custom_carrier_name", ""))
        (`$`<View>(R.id.hide_icon_bluetooth) as SwitchCompat).isChecked = prefs.getBoolean("hide_icon_bluetooth", false)
        (`$`<View>(R.id.hide_icon_hotspot) as SwitchCompat).isChecked = prefs.getBoolean("hide_icon_hotspot", false)
        (`$`<View>(R.id.hide_icon_debug) as SwitchCompat).isChecked = prefs.getBoolean("hide_icon_debug", false)
        (`$`<View>(R.id.hide_icon_save) as SwitchCompat).isChecked = prefs.getBoolean("hide_icon_save", false)
        (`$`<View>(R.id.hide_icon_alarm_clock) as SwitchCompat).isChecked = prefs.getBoolean("hide_icon_alarm_clock", false)
        (`$`<View>(R.id.hide_icon_volte) as SwitchCompat).isChecked = prefs.getBoolean("hide_icon_volte", false)
        (`$`<View>(R.id.hide_icon_shake) as SwitchCompat).isChecked = prefs.getBoolean("hide_icon_shake", false)
        (`$`<View>(R.id.hide_status_bar_no_sim_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_no_sim_icon", false)
        (`$`<View>(R.id.hide_status_bar_wifi_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_wifi_icon", false)
        (`$`<View>(R.id.hide_status_bar_vpn_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_vpn_icon", false)
        (`$`<View>(R.id.show_status_bar_time_second_icon) as SwitchCompat).isChecked = prefs.getBoolean("show_status_bar_time_second_icon", false)
        (`$`<View>(R.id.show_icon_battery_percentage) as SwitchCompat).isChecked = prefs.getBoolean("show_icon_battery_percentage", false)
        (`$`<View>(R.id.hide_status_bar_slow_rate_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_slow_rate_icon", false)
        (`$`<View>(R.id.hide_status_bar_time_week_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_time_week_icon", false)
        (`$`<View>(R.id.hide_status_bar_time_chinese_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_time_chinese_icon", false)
        (`$`<View>(R.id.status_text_view_clock_center) as SwitchCompat).isChecked = prefs.getBoolean("status_text_view_clock_center", false)
        (`$`<View>(R.id.hide_status_bar_sim1_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_sim1_icon", false)
        (`$`<View>(R.id.hide_status_bar_sim2_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_sim2_icon", false)
        (`$`<View>(R.id.hide_status_bar_location_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_location_icon", false)
        (`$`<View>(R.id.hide_status_bar_clock_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_clock_icon", false)
        (`$`<View>(R.id.hide_status_bar_battery_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_battery_icon", false)
        (`$`<View>(R.id.hide_status_bar_app_icon) as SwitchCompat).isChecked = prefs.getBoolean("hide_status_bar_app_icon", false)
        (`$`<View>(R.id.show_status_bar_time_am_pm) as SwitchCompat).isChecked = prefs.getBoolean("show_status_bar_time_am_pm", false)
        (`$`<View>(R.id.status_text_view_lyric_center) as SwitchCompat).isChecked = prefs.getBoolean("status_text_view_lyric_center", false)
        if (!Utils.check(SharedHelper(mContext))) {
            `$`<View>(R.id.hide_status_bar_slow_rate_icon).isEnabled = false
            `$`<View>(R.id.hide_status_bar_time_week_icon).isEnabled = false
            `$`<View>(R.id.hide_status_bar_time_chinese_icon).isEnabled = false
            `$`<View>(R.id.status_text_view_clock_center).isEnabled = false
            `$`<View>(R.id.hide_status_bar_app_icon).isEnabled = false
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        setUpData()
    }

    //location
    //bluetooth
    //zen
    //mute
    //wifi
    //battery
    //alarm_clock
    //clock
    fun updateIcon() {
        val hiddenIcons: MutableList<String> = ArrayList()
        hiddenIcons.add("rotate")
        hiddenIcons.add("headset")
        if (prefs.getBoolean("hide_status_bar_location_icon", false)) hiddenIcons.add("location")
        if (prefs.getBoolean("hide_icon_bluetooth", false)) hiddenIcons.add("bluetooth")
        if (prefs.getBoolean("hide_icon_shake", false)) {
            hiddenIcons.add("zen")
            hiddenIcons.add("volume")
            hiddenIcons.add("mute")
        }
        if (prefs.getBoolean("hide_status_bar_wifi_icon", false)) {
            hiddenIcons.add("wifi")
            hiddenIcons.add("dual_wifi")
        }
        if (prefs.getBoolean("hide_status_bar_battery_icon", false)) hiddenIcons.add("battery")
        if (prefs.getBoolean("hide_icon_alarm_clock", false)) hiddenIcons.add("alarm_clock")
        if (prefs.getBoolean("hide_status_bar_clock_icon", false)) hiddenIcons.add("clock")
        val icons: String
        icons = if (hiddenIcons.size == 0) {
            "null"
        } else {
            hiddenIcons.stream().collect(Collectors.joining(","))
        }
        Shell.su("settings put secure icon_blacklist $icons").exec()
    }
}