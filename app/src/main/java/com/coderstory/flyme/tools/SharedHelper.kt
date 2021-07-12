package com.coderstory.flyme.tools

import android.content.Context
import com.coderstory.flyme.preferences.PreferencesProviderUtils

class SharedHelper(private val context: Context) {
    var spName = "UserSettings"
    fun put(key: String, `object`: Any): Boolean {
        return if (`object` is String) {
            PreferencesProviderUtils.putString(context, spName, key, `object`)
        } else if (`object` is Int) {
            PreferencesProviderUtils.putInt(context, spName, key, `object`)
        } else if (`object` is Boolean) {
            PreferencesProviderUtils.putBoolean(context, spName, key, `object`)
        } else if (`object` is Float) {
            PreferencesProviderUtils.putFloat(context, spName, key, `object`)
        } else if (`object` is Long) {
            PreferencesProviderUtils.putLong(context, spName, key, `object`)
        } else {
            PreferencesProviderUtils.putString(context, spName, key, `object`.toString())
        }
    }

    fun getBoolean(key: String, defaultObject: Boolean): Boolean {
        return PreferencesProviderUtils.getBoolean(context, spName,key, defaultObject)
    }

    fun getString(key: String, defaultObject: String): String {
        return PreferencesProviderUtils.getString(context, spName, key, defaultObject)
    }
}