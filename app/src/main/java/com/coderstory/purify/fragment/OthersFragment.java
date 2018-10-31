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
            getEditor().putBoolean("EnableBlockAD", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });


        $(R.id.enabletheme).setOnClickListener(v -> {
            getEditor().putBoolean("EnableTheme", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.authcreak).setOnClickListener(v -> {
            getEditor().putBoolean("authcreak", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.zipauthcreak).setOnClickListener(v -> {
            getEditor().putBoolean("zipauthcreak", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.HideRootWithPay).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootWithPay", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.HideRootWithUpgrade).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootWithUpgrade", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });


        $(R.id.downgrade).setOnClickListener(v -> {
            getEditor().putBoolean("downgrade", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.hide_icon_label).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_label", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });
        $(R.id.hide_icon_5).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_5", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.enableCTS).setOnClickListener(v -> {
            getEditor().putBoolean("enableCTS", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.enableCheckInstaller).setOnClickListener(v -> {
            getEditor().putBoolean("enableCheckInstaller", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        $(R.id.enabletheme).setOnClickListener(v -> {
            getEditor().putBoolean("enabletheme", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });

        if (getPrefs().getString("platform", "").equals("")) {
            // 读取平台签名并保存
            new Thread(() -> {
                try {
                    PackageInfo packageInfo = getMContext().getPackageManager().getPackageInfo("android", PackageManager.GET_SIGNATURES);
                    if (packageInfo.signatures[0] != null) {
                        String platform = new String(Base64.encode(packageInfo.signatures[0].toByteArray(), Base64.DEFAULT)).replaceAll("\n", "");
                        getEditor().putString("platform", platform);
                        getEditor().apply();
                        sudoFixPermissions();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_others;
    }

    @Override
    protected void setUpData() {
        ((Switch) $(R.id.hide_icon_label)).setChecked(getPrefs().getBoolean("hide_icon_label", false));
        ((Switch) $(R.id.authcreak)).setChecked(getPrefs().getBoolean("authcreak", false));
        ((Switch) $(R.id.zipauthcreak)).setChecked(getPrefs().getBoolean("zipauthcreak", false));
        ((Switch) $(R.id.downgrade)).setChecked(getPrefs().getBoolean("downgrade", false));
        ((Switch) $(R.id.enableBlockAD)).setChecked(getPrefs().getBoolean("EnableBlockAD", false));
        ((Switch) $(R.id.enabletheme)).setChecked(getPrefs().getBoolean("enabletheme", false));
        ((Switch) $(R.id.HideRootWithPay)).setChecked(getPrefs().getBoolean("HideRootWithPay", false));
        ((Switch) $(R.id.HideRootWithUpgrade)).setChecked(getPrefs().getBoolean("HideRootWithUpgrade", false));
        ((Switch) $(R.id.hide_icon_5)).setChecked(getPrefs().getBoolean("hide_icon_5", false));
        ((Switch) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
        ((Switch) $(R.id.enableCTS)).setChecked(getPrefs().getBoolean("enableCTS", false));
    }

}
