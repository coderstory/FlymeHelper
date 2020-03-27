package com.coderstory.purify.fragment;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.widget.Switch;

import com.coderstory.purify.R;
import com.coderstory.purify.fragment.base.BaseFragment;
import com.coderstory.purify.utils.hostshelper.FileHelper;
import com.coderstory.purify.utils.hostshelper.HostsHelper;

import java.io.UnsupportedEncodingException;


public class OthersFragment extends BaseFragment {
    public OthersFragment() {
    }

    @Override
    protected void setUpView() {

        $(R.id.enableBlockAD).setOnClickListener(v -> {
            getPrefs().saveConfig("EnableBlockAD", ((Switch) v).isChecked());
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


        $(R.id.enabletheme).setOnClickListener(v -> getPrefs().saveConfig("enabletheme", ((Switch) v).isChecked()));
        $(R.id.HideRootWithPay).setOnClickListener(v -> getPrefs().saveConfig("HideRootWithPay", ((Switch) v).isChecked()));
        $(R.id.HideRootWithUpgrade).setOnClickListener(v -> getPrefs().saveConfig("HideRootWithUpgrade", ((Switch) v).isChecked()));
        $(R.id.hide_icon_label).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_label", ((Switch) v).isChecked()));
        $(R.id.hide_icon_45).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_4", ((Switch) v).isChecked()));
        $(R.id.hide_icon_5).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_5", ((Switch) v).isChecked()));
        $(R.id.hide_icon_6).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_6", ((Switch) v).isChecked()));
        $(R.id.hide_icon_7).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_7", ((Switch) v).isChecked()));
        $(R.id.enableCTS).setOnClickListener(v -> getPrefs().saveConfig("enableCTS", ((Switch) v).isChecked()));
        $(R.id.enableCheckInstaller).setOnClickListener(v -> getPrefs().saveConfig("enableCheckInstaller", ((Switch) v).isChecked()));
        $(R.id.hide_icon_alarm_clock).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_alarm_clock", ((Switch) v).isChecked()));
        $(R.id.hide_icon_bluetooth).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_bluetooth", ((Switch) v).isChecked()));
        $(R.id.hide_icon_hotspot).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_hotspot", ((Switch) v).isChecked()));
        $(R.id.hide_icon_alarm_clock).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_alarm_clock", ((Switch) v).isChecked()));
        $(R.id.hide_icon_volte).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_volte", ((Switch) v).isChecked()));
        $(R.id.hideDepWarn).setOnClickListener(v -> getPrefs().saveConfig("hideDepWarn", ((Switch) v).isChecked()));
        $(R.id.hide_icon_shake).setOnClickListener(v -> getPrefs().saveConfig("hide_icon_shake", ((Switch) v).isChecked()));
        $(R.id.hide_status_bar_no_sim_icon).setOnClickListener(v -> getPrefs().saveConfig("hide_status_bar_no_sim_icon", ((Switch) v).isChecked()));
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
        ((Switch) $(R.id.hide_icon_shake)).setChecked(getPrefs().getBoolean("hideDepWarn", false));
        ((Switch) $(R.id.hide_status_bar_no_sim_icon)).setChecked(getPrefs().getBoolean("hide_status_bar_no_sim_icon", false));
    }

}
