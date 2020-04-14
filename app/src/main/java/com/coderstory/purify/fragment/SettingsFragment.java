package com.coderstory.purify.fragment;


import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.widget.Switch;

import com.coderstory.flyme.R;
import com.coderstory.purify.fragment.base.BaseFragment;


public class SettingsFragment extends BaseFragment {
    public SettingsFragment() {
    }

    @Override
    protected void setUpView() {

        $(R.id.enableCheck).setOnClickListener(v -> {
            getPrefs().put("enableCheck", ((Switch) v).isChecked());
        });

        $(R.id.installType).setOnClickListener(v -> {
            getPrefs().put("installType", ((Switch) v).isChecked());
        });

        $(R.id.hideicon).setOnClickListener(v -> {
            getPrefs().put("hideIcon", ((Switch) v).isChecked());

            ComponentName localComponentName = new ComponentName(getMContext(), "com.coderstory.purify.activity.SplashActivity");
            PackageManager localPackageManager = getMContext().getPackageManager();
            localPackageManager.getComponentEnabledSetting(localComponentName);
            PackageManager packageManager = getMContext().getPackageManager();
            ComponentName componentName = new ComponentName(getMContext(), "com.coderstory.purify.activity.SplashActivity");

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
        ((Switch) $(R.id.installType)).setChecked(getPrefs().getBoolean("installType", false));
    }
}
