package com.coderstory.FTool.fragment;


import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.widget.Switch;

import com.coderstory.FTool.R;

import ren.solid.library.fragment.base.BaseFragment;

public class SettingsFragment extends BaseFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void setUpView() {


        $(R.id.enableCheck).setOnClickListener(v -> {
            getEditor().putBoolean("enableCheck", ((Switch) v).isChecked());
            getEditor().apply();
        });

        $(R.id.hideicon).setOnClickListener(v -> {
            getEditor().putBoolean("hideicon", ((Switch) v).isChecked());
            getEditor().apply();
            ComponentName localComponentName = new ComponentName(getMContext(), "com.coderstory.FTool.activity.SplashActivity");
            PackageManager localPackageManager = getMContext().getPackageManager();
            localPackageManager.getComponentEnabledSetting(localComponentName);
            PackageManager packageManager = getMContext().getPackageManager();
            ComponentName componentName = new ComponentName(getMContext(), "com.coderstory.FTool.activity.SplashActivity");

            if (((Switch) v).isChecked()) {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP);
            }

        });


    }

    @Override
    protected void setUpData() {
        ((Switch) $(R.id.enableCheck)).setChecked(getPrefs().getBoolean("enableCheck", true));
        ((Switch) $(R.id.hideicon)).setChecked(getPrefs().getBoolean("hideicon", false));
    }

}
