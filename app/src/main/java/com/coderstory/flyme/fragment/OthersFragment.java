package com.coderstory.flyme.fragment;


import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.SuHelper;
import com.coderstory.flyme.utils.hostshelper.FileHelper;
import com.coderstory.flyme.utils.hostshelper.HostsHelper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import eu.chainfire.libsuperuser.Shell;


public class OthersFragment extends BaseFragment {
    public OthersFragment() {
    }

    @Override
    protected void setUpView() {

        setDatePickerDividerColor($(R.id.home_icon_num_column));
        setDatePickerDividerColor($(R.id.home_icon_num_rows));
        setDatePickerDividerColor($(R.id.home_icon_num_hot_seat_icons));


        $(R.id.enableBlockAD).setOnClickListener(v -> {
            getEditor().putBoolean("EnableBlockAD", ((Switch) v).isChecked());
            fix();
            FileHelper fh = new FileHelper();
            String HostsContext = fh.getFromAssets("hosts_default", getMContext());

            if (((Switch) v).isChecked()) {
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

        $(R.id.enableCTS).setOnClickListener(v -> {
            getEditor().putBoolean("enableCTS", ((Switch) v).isChecked());
            fix();
        });
        $(R.id.enableCheckInstaller).setOnClickListener(v -> {
            getEditor().putBoolean("enableCheckInstaller", ((Switch) v).isChecked());
            fix();
        });

        $(R.id.hideDepWarn).setOnClickListener(v -> {
            getEditor().putBoolean("hideDepWarn", ((Switch) v).isChecked());
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
        $(R.id.HideRootGlobal).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootGlobal", ((Switch) v).isChecked());
            fix();
        });

        ((NumberPicker) $(R.id.home_icon_num_column)).setOnValueChangedListener((v, oldValue, newValue) -> {
            getEditor().putInt("home_icon_num_column", newValue);
            fix();
        });
        ((NumberPicker) $(R.id.home_icon_num_rows)).setOnValueChangedListener((v, oldValue, newValue) -> {
            getEditor().putInt("home_icon_num_rows", newValue);
            fix();
        });

        ((NumberPicker) $(R.id.home_icon_num_hot_seat_icons)).setOnValueChangedListener((v, oldValue, newValue) -> {
            getEditor().putInt("home_icon_num_hot_seat_icons", newValue);
            fix();
        });
        copySo();
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
        ((Switch) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
        ((Switch) $(R.id.enableCTS)).setChecked(getPrefs().getBoolean("enableCTS", false));
        ((Switch) $(R.id.hideDepWarn)).setChecked(getPrefs().getBoolean("hideDepWarn", false));
        ((Switch) $(R.id.removeStore)).setChecked(getPrefs().getBoolean("removeStore", false));
        ((Switch) $(R.id.autoInstall)).setChecked(getPrefs().getBoolean("autoInstall", false));
        ((Switch) $(R.id.HideRootGlobal)).setChecked(getPrefs().getBoolean("HideRootGlobal", false));
        ((NumberPicker) $(R.id.home_icon_num_column)).setValue(getPrefs().getInt("home_icon_num_column", 4));
        ((NumberPicker) $(R.id.home_icon_num_rows)).setValue(getPrefs().getInt("home_icon_num_rows", 5));
        ((NumberPicker) $(R.id.home_icon_num_hot_seat_icons)).setValue(getPrefs().getInt("home_icon_num_hot_seat_icons", 4));
    }

    private void setDatePickerDividerColor(NumberPicker picker) {

        //设置最大值
        picker.setMaxValue(7);
        //设置最小值
        picker.setMinValue(4);

        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, new ColorDrawable(Color.alpha(256)));
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public void copySo(){
        ///data/app/com.coderstory.flyme-BXZlEdHOp7SsF02Yd3u8BA==/base.apk
        String path =  getMContext().getPackageResourcePath().replace("/base.apk","")+"/lib/arm64/libnc.so";
        Shell.SU.run("echo "+path+" > /data/config.cfg");
        Shell.SU.run("chmod 0777 "+path+" /data/config.cfg");
    }
}
