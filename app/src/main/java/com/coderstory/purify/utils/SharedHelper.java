package com.coderstory.purify.utils;

import android.content.Context;

import com.zlm.libs.preferences.PreferencesProviderUtils;

public class SharedHelper {
    String spName = "config";
    private Context context;

    public SharedHelper(Context context) {
        this.context = context;
    }

    public void put(String key, Object object) {

        if (object instanceof String) {
            PreferencesProviderUtils.putString(context, spName, key, (String) object);
        } else if (object instanceof Integer) {
            PreferencesProviderUtils.putInt(context, spName, key, (Integer) object);
        } else if (object instanceof Boolean) {
            PreferencesProviderUtils.putBoolean(context, spName, key, (Boolean) object);
        } else if (object instanceof Float) {
            PreferencesProviderUtils.putFloat(context, spName, key, (Float) object);
        } else if (object instanceof Long) {
            PreferencesProviderUtils.putLong(context, spName, key, (Long) object);
        } else {
            PreferencesProviderUtils.putString(context, spName, key, object.toString());
        }
    }


    public boolean getBoolean(String key, boolean defaultObject) {
        return PreferencesProviderUtils.getBoolean(context, spName, key, defaultObject);
    }

    public String getString(String key, String defaultObject) {
        return PreferencesProviderUtils.getString(context, spName, key, defaultObject);
    }
}
