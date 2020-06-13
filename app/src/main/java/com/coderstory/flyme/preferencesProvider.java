package com.coderstory.flyme;

import com.coderstory.flyme.preferences.PreferencesProvider;

public class preferencesProvider extends PreferencesProvider {
    @Override
    public String getAuthorities() {
        return "com.coderstory.flyme.preferencesProvider";
    }
}
