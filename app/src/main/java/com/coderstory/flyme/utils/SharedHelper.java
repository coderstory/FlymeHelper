package com.coderstory.flyme.utils;

import android.content.Context;

import com.coderstory.flyme.preferences.PreferencesProviderUtils;

public class SharedHelper {
    String spName = "UserSettings";
    private Context context;

    public SharedHelper(Context context) {
        this.context = context;
    }

    public boolean put(String key, Object object) {

        if (object instanceof String) {
            return PreferencesProviderUtils.putString(context, spName, key, (String) object);
        } else if (object instanceof Integer) {
            return PreferencesProviderUtils.putInt(context, spName, key, (Integer) object);
        } else if (object instanceof Boolean) {
            return PreferencesProviderUtils.putBoolean(context, spName, key, (Boolean) object);
        } else if (object instanceof Float) {
            return PreferencesProviderUtils.putFloat(context, spName, key, (Float) object);
        } else if (object instanceof Long) {
            return PreferencesProviderUtils.putLong(context, spName, key, (Long) object);
        } else {
            return PreferencesProviderUtils.putString(context, spName, key, object.toString());
        }
    }


    public boolean getBoolean(String key, boolean defaultObject) {
        return PreferencesProviderUtils.getBoolean(context, spName, key, defaultObject);
    }

    public String getString(String key, String defaultObject) {
        return PreferencesProviderUtils.getString(context, spName, key, defaultObject);
    }
}
