package com.coderstory.flyme.fragment;

import android.widget.Switch;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;

import eu.chainfire.libsuperuser.Shell;

public class SystemUIFragment extends BaseFragment {
    @Override
    protected void setUpView() {
        $(R.id.hide_icon_alarm_clock).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_alarm_clock", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_bluetooth).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_bluetooth", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_hotspot).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_hotspot", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_volte).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_volte", ((Switch) v).isChecked());
            fix();
        });

        $(R.id.hide_icon_shake).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_shake", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_status_bar_no_sim_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_no_sim_icon", ((Switch) v).isChecked());
            fix();
        });

        $(R.id.hide_status_bar_wifi_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_wifi_icon", ((Switch) v).isChecked());
            fix();
        });

        $(R.id.hide_status_bar_vpn_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_vpn_icon", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.show_icon_battery_percentage).setOnClickListener(v -> {
            getEditor().putBoolean("show_icon_battery_percentage", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.show_status_bar_time_second_icon).setOnClickListener(v -> {
            getEditor().putBoolean("show_status_bar_time_second_icon", ((Switch) v).isChecked());
            fix();
            Shell.SU.run("settings put secure clock_seconds " + (((Switch) v).isChecked() ? "1" : "0"));
        });
        $(R.id.hide_status_bar_slow_rate_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_slow_rate_icon", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_status_bar_time_week_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_time_week_icon", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_status_bar_time_chinese_icon).setOnClickListener(v -> {
            getEditor().putBoolean("hide_status_bar_time_chinese_icon", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.status_text_view_clock_center).setOnClickListener(v -> {
            getEditor().putBoolean("status_text_view_clock_center", ((Switch) v).isChecked());
            fix();
        });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_system_ui;
    }

    @Override
    protected void setUpData() {
        ((Switch) $(R.id.hide_icon_bluetooth)).setChecked(getPrefs().getBoolean("hide_icon_bluetooth", false));
        ((Switch) $(R.id.hide_icon_hotspot)).setChecked(getPrefs().getBoolean("hide_icon_hotspot", false));
        ((Switch) $(R.id.hide_icon_alarm_clock)).setChecked(getPrefs().getBoolean("hide_icon_alarm_clock", false));
        ((Switch) $(R.id.hide_icon_volte)).setChecked(getPrefs().getBoolean("hide_icon_volte", false));
        ((Switch) $(R.id.hide_icon_shake)).setChecked(getPrefs().getBoolean("hide_icon_shake", false));
        ((Switch) $(R.id.hide_status_bar_no_sim_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_no_sim_icon", false));
        ((Switch) $(R.id.hide_status_bar_wifi_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_wifi_icon", false));
        ((Switch) $(R.id.hide_status_bar_vpn_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_vpn_icon", false));
        ((Switch) $(R.id.show_status_bar_time_second_icon)).setChecked(getPrefs().getBoolean("show_status_bar_time_second_icon", false));
        ((Switch) $(R.id.show_icon_battery_percentage)).setChecked(getPrefs().getBoolean("show_icon_battery_percentage", false));
        ((Switch) $(R.id.hide_status_bar_slow_rate_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_slow_rate_icon", false));
        ((Switch) $(R.id.hide_status_bar_time_week_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_time_week_icon", false));
        ((Switch) $(R.id.hide_status_bar_time_chinese_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_time_chinese_icon", false));
        ((Switch) $(R.id.status_text_view_clock_center)).setChecked(getPrefs().getBoolean("status_text_view_clock_center", false));
        if (getPrefs().getString("qq", "").equals("") || getPrefs().getString("uuid", "").equals("")) {
            ((Switch) $(R.id.hide_status_bar_slow_rate_icon)).setEnabled(false);
            ((Switch) $(R.id.hide_status_bar_time_week_icon)).setEnabled(false);
            ((Switch) $(R.id.hide_status_bar_time_chinese_icon)).setEnabled(false);
            ((Switch) $(R.id.status_text_view_clock_center)).setEnabled(false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        setUpData();
    }
}
