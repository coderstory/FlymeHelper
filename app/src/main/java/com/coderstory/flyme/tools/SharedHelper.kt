package com.coderstory.flyme.tools

import android.content.Context
import com.coderstory.flyme.preferences.PreferencesProviderUtils

class SharedHelper(private val context: Context) {
    var spName = "UserSettings"
    fun put(key: String, value: Any): Boolean {
        return if (value is String) {
            PreferencesProviderUtils.putString(context, spName, key, value)
        } else if (value is Int) {
            PreferencesProviderUtils.putInt(context, spName, key, value)
        } else if (value is Boolean) {
            PreferencesProviderUtils.putBoolean(context, spName, key, value)
        } else if (value is Float) {
            PreferencesProviderUtils.putFloat(context, spName, key, value)
        } else if (value is Long) {
            PreferencesProviderUtils.putLong(context, spName, key, value)
        } else {
            PreferencesProviderUtils.putString(context, spName, key, value.toString())
        }
    }

    fun getBoolean(key: String, defaultObject: Boolean): Boolean {
        return PreferencesProviderUtils.getBoolean(context, spName, key, defaultObject)
    }

    fun getString(key: String, defaultObject: String): String {
        return PreferencesProviderUtils.getString(context, spName, key, defaultObject)
    }
}