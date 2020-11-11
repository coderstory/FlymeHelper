package com.coderstory.flyme.fragment;


import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.widget.Switch;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;


public class SettingsFragment extends BaseFragment {
    public SettingsFragment() {
    }

    @Override
    protected void setUpView() {

        $(R.id.enableCheck).setOnClickListener(v -> {
            getEditor().putBoolean("enableCheck", ((Switch) v).isChecked());
            fix();
        });

        $(R.id.enableUpdate).setOnClickListener(v -> {
            getEditor().putBoolean("enableUpdate", ((Switch) v).isChecked());
            fix();
        });

        $(R.id.hideicon).setOnClickListener(v -> {
            getEditor().putBoolean("hideIcon", ((Switch) v).isChecked());
            fix();

            ComponentName localComponentName = new ComponentName(getMContext(), "com.coderstory.flyme.activity.SplashActivity");
            PackageManager localPackageManager = getMContext().getPackageManager();
            localPackageManager.getComponentEnabledSetting(localComponentName);
            PackageManager packageManager = getMContext().getPackageManager();
            ComponentName componentName = new ComponentName(getMContext(), "com.coderstory.flyme.activity.SplashActivity");

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
    protected int setLayoutResourceID() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void setUpData() {
        ((Switch) $(R.id.enableCheck)).setChecked(getPrefs().getBoolean("enableCheck", true));
        ((Switch) $(R.id.hideicon)).setChecked(getPrefs().getBoolean("hideIcon", false));
        ((Switch) $(R.id.enableUpdate)).setChecked(getPrefs().getBoolean("enableUpdate", true));
    }
}
