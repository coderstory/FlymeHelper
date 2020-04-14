package com.coderstory.purify;

import com.coderstory.purify.preferences.PreferencesProvider;

public class preferencesProvider extends PreferencesProvider {
    @Override
    public String getAuthorities() {
        return "com.coderstory.purify.preferencesProvider";
    }
}
