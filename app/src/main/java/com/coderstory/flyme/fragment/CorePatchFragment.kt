package com.coderstory.flyme.fragment


import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment

class CorePatchFragment : BaseFragment() {
    override fun setUpView() {
        `$`<View>(R.id.enhancedMode).setOnClickListener { v: View ->
            editor.putBoolean("enhancedMode", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.downgrade).setOnClickListener { v: View ->
            editor.putBoolean("downgrade", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.digestCreak).setOnClickListener { v: View ->
            editor.putBoolean("digestCreak", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.authcreak).setOnClickListener { v: View ->
            editor.putBoolean("authcreak", (v as SwitchCompat).isChecked)
            fix()
        }
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_core_patch
    }

    override fun setUpData() {
        (`$`<View>(R.id.authcreak) as SwitchCompat).isChecked = prefs.getBoolean("authcreak", false)
        (`$`<View>(R.id.digestCreak) as SwitchCompat).isChecked = prefs.getBoolean("digestCreak", false)
        (`$`<View>(R.id.downgrade) as SwitchCompat).isChecked = prefs.getBoolean("downgrade", false)
        (`$`<View>(R.id.enhancedMode) as SwitchCompat).isChecked = prefs.getBoolean("enhancedMode", false)
    }
}