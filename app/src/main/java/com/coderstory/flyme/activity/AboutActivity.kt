package com.coderstory.flyme.activity;


import androidx.fragment.app.Fragment;

import com.coderstory.flyme.fragment.AboutFragment;

public class AboutActivity extends ToolbarActivity {


    @Override
    protected Fragment setFragment() {
        return new AboutFragment();
    }

    @Override
    protected String getToolbarTitle() {
        return "关于";
    }
}
