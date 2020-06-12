package com.coderstory.purify.fragment;


import android.widget.Switch;

import com.coderstory.flyme.R;
import com.coderstory.purify.fragment.base.BaseFragment;
import com.coderstory.purify.utils.hostshelper.FileHelper;
import com.coderstory.purify.utils.hostshelper.HostsHelper;

import java.io.UnsupportedEncodingException;

import eu.chainfire.libsuperuser.Shell;


public class OthersFragment extends BaseFragment {
    public OthersFragment() {
    }

    @Override
    protected void setUpView() {

        $(R.id.enableBlockAD).setOnClickListener(v -> {
            getEditor().putBoolean("EnableBlockAD", ((Switch) v).isChecked());
            fix();
            FileHelper fh = new FileHelper();
            String HostsContext = fh.getFromAssets("hosts_default", getMContext());

            if (((Switch) v).isChecked()) { //如果未启用hosts
                HostsContext += fh.getFromAssets("hosts_noad", getMContext());
                HostsContext += fh.getFromAssets("hosts_Flyme", getMContext());
            }
            HostsHelper h = new HostsHelper(HostsContext, getMContext());
            try {
                h.execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });


        $(R.id.enabletheme).setOnClickListener(v -> {
            getEditor().putBoolean("enabletheme", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.HideRootWithPay).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootWithPay", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.HideRootWithUpgrade).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootWithUpgrade", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_label).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_label", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_45).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_4", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_5).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_5", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_6).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_6", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_7).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_7", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.enableCTS).setOnClickListener(v -> {
            getEditor().putBoolean("enableCTS", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.enableCheckInstaller).setOnClickListener(v -> {
            getEditor().putBoolean("enableCheckInstaller", ((Switch) v).isChecked());
            fix();
        });
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
        $(R.id.hide_icon_alarm_clock).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_alarm_clock", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_volte).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_volte", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.hideDepWarn).setOnClickListener(v -> {
            getEditor().putBoolean("hideDepWarn", ((Switch) v).isChecked());
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

        $(R.id.removeStore).setOnClickListener(v -> {
            getEditor().putBoolean("removeStore", ((Switch) v).isChecked());
            fix();
        });

        $(R.id.autoInstall).setOnClickListener(v -> {
            getEditor().putBoolean("autoInstall", ((Switch) v).isChecked());
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
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_others;
    }

    @Override
    protected void setUpData() {
        ((Switch) $(R.id.hide_icon_label)).setChecked(getPrefs().getBoolean("hide_icon_label", false));
        ((Switch) $(R.id.enableBlockAD)).setChecked(getPrefs().getBoolean("EnableBlockAD", false));
        ((Switch) $(R.id.enabletheme)).setChecked(getPrefs().getBoolean("enabletheme", false));
        ((Switch) $(R.id.HideRootWithPay)).setChecked(getPrefs().getBoolean("HideRootWithPay", false));
        ((Switch) $(R.id.HideRootWithUpgrade)).setChecked(getPrefs().getBoolean("HideRootWithUpgrade", false));
        ((Switch) $(R.id.hide_icon_45)).setChecked(getPrefs().getBoolean("hide_icon_4", false));
        ((Switch) $(R.id.hide_icon_5)).setChecked(getPrefs().getBoolean("hide_icon_5", false));
        ((Switch) $(R.id.hide_icon_6)).setChecked(getPrefs().getBoolean("hide_icon_6", false));
        ((Switch) $(R.id.hide_icon_7)).setChecked(getPrefs().getBoolean("hide_icon_7", false));
        ((Switch) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
        ((Switch) $(R.id.enableCTS)).setChecked(getPrefs().getBoolean("enableCTS", false));
        ((Switch) $(R.id.hide_icon_bluetooth)).setChecked(getPrefs().getBoolean("hide_icon_bluetooth", false));
        ((Switch) $(R.id.hide_icon_hotspot)).setChecked(getPrefs().getBoolean("hide_icon_hotspot", false));
        ((Switch) $(R.id.hide_icon_alarm_clock)).setChecked(getPrefs().getBoolean("hide_icon_alarm_clock", false));
        ((Switch) $(R.id.hide_icon_volte)).setChecked(getPrefs().getBoolean("hide_icon_volte", false));
        ((Switch) $(R.id.hideDepWarn)).setChecked(getPrefs().getBoolean("hideDepWarn", false));
        ((Switch) $(R.id.hide_icon_shake)).setChecked(getPrefs().getBoolean("hide_icon_shake", false));
        ((Switch) $(R.id.hide_status_bar_no_sim_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_no_sim_icon", false));
        ((Switch) $(R.id.removeStore)).setChecked(getPrefs().getBoolean("removeStore", false));
        ((Switch) $(R.id.autoInstall)).setChecked(getPrefs().getBoolean("autoInstall", false));
        ((Switch) $(R.id.hide_status_bar_wifi_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_wifi_icon", false));
        ((Switch) $(R.id.hide_status_bar_vpn_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_vpn_icon", false));
        ((Switch) $(R.id.show_status_bar_time_second_icon)).setChecked(getPrefs().getBoolean("show_status_bar_time_second_icon", false));
        ((Switch) $(R.id.show_icon_battery_percentage)).setChecked(getPrefs().getBoolean("show_icon_battery_percentage", false));
    }
}
