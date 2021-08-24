package com.coderstory.flyme.fragment


import android.content.ComponentName
import android.content.pm.PackageManager
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment

class SettingsFragment : BaseFragment() {
    override fun setUpView() {
        `$`<View>(R.id.enableCheck).setOnClickListener { v: View ->
            editor.putBoolean("enableCheck", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hideicon).setOnClickListener { v: View ->
            editor.putBoolean("hideIcon", (v as SwitchCompat).isChecked)
            fix()
            val localComponentName = ComponentName(mContext, "com.coderstory.flyme.activity.SplashActivity")
            val localPackageManager = mContext.packageManager
            localPackageManager.getComponentEnabledSetting(localComponentName)
            val packageManager = mContext.packageManager
            val componentName = ComponentName(mContext, "com.coderstory.flyme.activity.SplashActivity")
            if (v.isChecked) {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP)
            } else {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP)
            }
        }
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_settings
    }

    override fun setUpData() {
        (`$`<View>(R.id.enableCheck) as SwitchCompat).isChecked = prefs.getBoolean("enableCheck", true)
        (`$`<View>(R.id.hideicon) as SwitchCompat).isChecked = prefs.getBoolean("hideIcon", false)
    }
}