package com.coderstory.flyme.fragment;


import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;


public class CorePatchFragment extends BaseFragment {


    public CorePatchFragment() {
    }

    @Override
    protected void setUpView() {
        $(R.id.enhancedMode).setOnClickListener(v -> {
            getEditor().putBoolean("enhancedMode", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.downgrade).setOnClickListener(v -> {
            getEditor().putBoolean("downgrade", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.digestCreak).setOnClickListener(v -> {
            getEditor().putBoolean("digestCreak", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.authcreak).setOnClickListener(v -> {
            getEditor().putBoolean("authcreak", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_core_patch;
    }

    @Override
    protected void setUpData() {
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.authcreak)).setChecked(getPrefs().getBoolean("authcreak", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.digestCreak)).setChecked(getPrefs().getBoolean("digestCreak", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.downgrade)).setChecked(getPrefs().getBoolean("downgrade", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.enhancedMode)).setChecked(getPrefs().getBoolean("enhancedMode", false));
    }
}
