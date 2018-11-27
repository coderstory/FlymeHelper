package com.coderstory.purify.fragment;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.widget.Switch;

import com.coderstory.purify.R;
import com.coderstory.purify.fragment.base.BaseFragment;


public class OthersFragment extends BaseFragment {
    public OthersFragment() {
    }

    @Override
    protected void setUpView() {

        $(R.id.enableBlockAD).setOnClickListener(v -> {
            getPrefs().saveConfig("EnableBlockAD", ((Switch) v).isChecked());

        });


        $(R.id.enabletheme).setOnClickListener(v -> {
            getPrefs().saveConfig("EnableTheme", ((Switch) v).isChecked());

        });

        $(R.id.HideRootWithPay).setOnClickListener(v -> {
            getPrefs().saveConfig("HideRootWithPay", ((Switch) v).isChecked());

        });

        $(R.id.HideRootWithUpgrade).setOnClickListener(v -> {
            getPrefs().saveConfig("HideRootWithUpgrade", ((Switch) v).isChecked());

        });


        $(R.id.hide_icon_label).setOnClickListener(v -> {
            getPrefs().saveConfig("hide_icon_label", ((Switch) v).isChecked());

        });
        $(R.id.hide_icon_5).setOnClickListener(v -> {
            getPrefs().saveConfig("hide_icon_5", ((Switch) v).isChecked());

        });

        $(R.id.enableCTS).setOnClickListener(v -> {
            getPrefs().saveConfig("enableCTS", ((Switch) v).isChecked());

        });

        $(R.id.enableCheckInstaller).setOnClickListener(v -> {
            getPrefs().saveConfig("enableCheckInstaller", ((Switch) v).isChecked());

        });

        $(R.id.enabletheme).setOnClickListener(v -> {
            getPrefs().saveConfig("enabletheme", ((Switch) v).isChecked());

        });

        if (getPrefs().getString("platform", "").equals("")) {
            // 读取平台签名并保存
            PackageInfo packageInfo = null;
            try {
                packageInfo = getMContext().getPackageManager().getPackageInfo("android", PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageInfo.signatures[0] != null) {
                String platform = new String(Base64.encode(packageInfo.signatures[0].toByteArray(), Base64.DEFAULT)).replaceAll("\n", "");
                getPrefs().saveConfig("platform", platform);
            }
        }
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
        ((Switch) $(R.id.hide_icon_5)).setChecked(getPrefs().getBoolean("hide_icon_5", false));
        ((Switch) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
        ((Switch) $(R.id.enableCTS)).setChecked(getPrefs().getBoolean("enableCTS", false));
    }

}
