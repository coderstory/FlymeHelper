package com.coderstory.flyme.fragment;


import android.content.ComponentName;
import android.content.pm.PackageManager;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;


public class SettingsFragment extends BaseFragment {
    public SettingsFragment() {
    }

    @Override
    protected void setUpView() {

        $(R.id.enableCheck).setOnClickListener(v -> {
            getEditor().putBoolean("enableCheck", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.enableUpdate).setOnClickListener(v -> {
            getEditor().putBoolean("enableUpdate", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.hideicon).setOnClickListener(v -> {
            getEditor().putBoolean("hideIcon", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();

            ComponentName localComponentName = new ComponentName(getMContext(), "com.coderstory.flyme.activity.SplashActivity");
            PackageManager localPackageManager = getMContext().getPackageManager();
            localPackageManager.getComponentEnabledSetting(localComponentName);
            PackageManager packageManager = getMContext().getPackageManager();
            ComponentName componentName = new ComponentName(getMContext(), "com.coderstory.flyme.activity.SplashActivity");

            if (((androidx.appcompat.widget.SwitchCompat) v).isChecked()) {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP);
            }
        });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void setUpData() {
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.enableCheck)).setChecked(getPrefs().getBoolean("enableCheck", true));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hideicon)).setChecked(getPrefs().getBoolean("hideIcon", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.enableUpdate)).setChecked(getPrefs().getBoolean("enableUpdate", true));
    }
}
