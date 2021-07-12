package com.coderstory.flyme.fragment;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private final Bundle args;
    private final List<Fragment> fragments;

    TabFragmentAdapter(FragmentManager fm, List<Fragment> fragments, Bundle args) {
        super(fm);
        this.fragments = fragments;
        this.args = args;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
