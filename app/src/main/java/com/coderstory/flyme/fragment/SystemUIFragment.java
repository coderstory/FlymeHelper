package com.coderstory.flyme.fragment;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.tools.SharedHelper;
import com.coderstory.flyme.tools.Utils;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SystemUIFragment extends BaseFragment {
    @Override
    protected void setUpView() {
        $(R.id.hide_icon_alarm_clock).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_alarm_clock", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            updateIcon();
        });
        $(R.id.hide_icon_bluetooth).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_bluetooth", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            updateIcon();
        });
        $(R.id.hide_icon_hotspot).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_hotspot", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_save).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_save", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_debug).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_debug", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_volte).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_volte", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.hide_icon_shake).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_shake", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            updateIcon();
        });
        $(R.id.hide_status_bar_no_sim_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_no_sim_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.hide_status_bar_wifi_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_wifi_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            updateIcon();
        });

        $(R.id.hide_status_bar_vpn_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_vpn_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.show_icon_battery_percentage).setOnClickListener(v -> {
            getEditor().putBoolean("show_icon_battery_percentage", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.show_status_bar_time_second_icon).setOnClickListener(v -> {
            getEditor().putBoolean("show_status_bar_time_second_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            Shell.su("settings put secure clock_seconds " + (((androidx.appcompat.widget.SwitchCompat) v).isChecked() ? "1" : "0")).exec();
        });
        $(R.id.hide_status_bar_slow_rate_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_slow_rate_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_status_bar_time_week_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_time_week_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_status_bar_time_chinese_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_time_chinese_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.status_text_view_clock_center).setOnClickListener(v -> {
            getEditor().putBoolean("status_text_view_clock_center", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_status_bar_sim1_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_sim1_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_status_bar_sim2_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_sim2_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.hide_status_bar_location_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_location_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            updateIcon();
        });

        $(R.id.hide_status_bar_clock_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_clock_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            updateIcon();
        });

        $(R.id.hide_status_bar_battery_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_battery_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            updateIcon();
        });
        $(R.id.hide_status_bar_app_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_app_icon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.show_status_bar_time_am_pm).setOnClickListener(v -> {
            getEditor().putBoolean("show_status_bar_time_am_pm", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_system_ui;
    }

    @Override
    protected void setUpData() {
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_bluetooth)).setChecked(getPrefs().getBoolean("hide_icon_bluetooth", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_hotspot)).setChecked(getPrefs().getBoolean("hide_icon_hotspot", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_debug)).setChecked(getPrefs().getBoolean("hide_icon_debug", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_save)).setChecked(getPrefs().getBoolean("hide_icon_save", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_alarm_clock)).setChecked(getPrefs().getBoolean("hide_icon_alarm_clock", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_volte)).setChecked(getPrefs().getBoolean("hide_icon_volte", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_shake)).setChecked(getPrefs().getBoolean("hide_icon_shake", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_no_sim_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_no_sim_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_wifi_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_wifi_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_vpn_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_vpn_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.show_status_bar_time_second_icon)).setChecked(getPrefs().getBoolean("show_status_bar_time_second_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.show_icon_battery_percentage)).setChecked(getPrefs().getBoolean("show_icon_battery_percentage", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_slow_rate_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_slow_rate_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_time_week_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_time_week_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_time_chinese_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_time_chinese_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.status_text_view_clock_center)).setChecked(getPrefs().getBoolean("status_text_view_clock_center", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_sim1_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_sim1_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_sim2_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_sim2_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_location_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_location_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_clock_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_clock_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_battery_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_battery_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_status_bar_app_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_app_icon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.show_status_bar_time_am_pm)).setChecked(getPrefs().getBoolean("show_status_bar_time_am_pm", false));

        if (!Utils.check(new SharedHelper(getMContext()))) {
            $(R.id.hide_status_bar_slow_rate_icon).setEnabled(false);
            $(R.id.hide_status_bar_time_week_icon).setEnabled(false);
            $(R.id.hide_status_bar_time_chinese_icon).setEnabled(false);
            $(R.id.status_text_view_clock_center).setEnabled(false);
            $(R.id.hide_status_bar_app_icon).setEnabled(false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        setUpData();
    }

    //location
    //bluetooth
    //zen
    //mute
    //wifi
    //battery
    //alarm_clock
    //clock
    public void updateIcon() {
        List<String> hiddenIcons = new ArrayList<>();
        hiddenIcons.add("rotate");
        hiddenIcons.add("headset");

        if (getPrefs().getBoolean("hide_status_bar_location_icon", false))
            hiddenIcons.add("location");
        if (getPrefs().getBoolean("hide_icon_bluetooth", false))
            hiddenIcons.add("bluetooth");
        if (getPrefs().getBoolean("hide_icon_shake", false)) {
            hiddenIcons.add("zen");
            hiddenIcons.add("volume");
            hiddenIcons.add("mute");
        }
        if (getPrefs().getBoolean("hide_status_bar_wifi_icon", false)) {
            hiddenIcons.add("wifi");
            hiddenIcons.add("dual_wifi");
        }
        if (getPrefs().getBoolean("hide_status_bar_battery_icon", false))
            hiddenIcons.add("battery");
        if (getPrefs().getBoolean("hide_icon_alarm_clock", false))
            hiddenIcons.add("alarm_clock");
        if (getPrefs().getBoolean("hide_status_bar_clock_icon", false)) hiddenIcons.add("clock");

        String icons;
        if (hiddenIcons.size() == 0) {
            icons = "null";
        } else {
            icons = hiddenIcons.stream().collect(Collectors.joining(","));
        }
        Shell.su("settings put secure icon_blacklist " + icons).exec();
    }
}
