package com.coderstory.flyme10.preferences


import android.content.*
import com.coderstory.flyme10.tools.*

/**
 * PreferencesUtils, easy to get or put data
 *
 * **Preference Name**
 *  * you can change preference name by [.]
 *
 *
 * **Put Value**
 *  * put string [.putString]
 *  * put int [.putInt]
 *  * put long [.putLong]
 *  * put float [.putFloat]
 *  * put boolean [.putBoolean]
 *
 *
 * **Get Value**
 *  * get string [.getString], [.getString]
 *  * get int [.getInt], [.getInt]
 *  * get long [.getLong], [.getLong]
 *  * get float [.getFloat], [.getFloat]
 *  * get boolean [.getBoolean], [.getBoolean]
 *
 *
 * @author [Trinea](http://www.trinea.cn) 2013-3-6
 */
object PreferencesUtils {
    /**
     * put string preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putString(context: Context?, spName: String?, key: String?, value: String?): Boolean {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        val editor = settings.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun remove(context: Context?, spName: String?, key: String?): Boolean {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        val editor = settings.edit()
        editor.remove(key)
        return editor.commit()
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     * name that is not a string
     * @see .getString
     */
    fun getString(context: Context, spName: String, key: String): String {
        return getString(context, spName, key, "")
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a string
     */
    fun getString(context: Context, spName: String, key: String, defaultValue: String): String {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        return settings.getString(key, defaultValue) ?: ""
    }

    /**
     * put int preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putInt(context: Context?, spName: String?, key: String?, value: Int): Boolean {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        val editor = settings.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a int
     * @see .getInt
     */
    fun getInt(context: Context?, spName: String?, key: String?): Int {
        return getInt(context, spName, key, -1)
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a int
     */
    fun getInt(context: Context?, spName: String?, key: String?, defaultValue: Int): Int {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        return settings.getInt(key, defaultValue)
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putLong(context: Context?, spName: String?, key: String?, value: Long): Boolean {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        val editor = settings.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a long
     * @see .getLong
     */
    fun getLong(context: Context?, spName: String?, key: String?): Long {
        return getLong(context, spName, key, -1)
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a long
     */
    fun getLong(context: Context?, spName: String?, key: String?, defaultValue: Long): Long {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        return settings.getLong(key, defaultValue)
    }

    /**
     * put float preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putFloat(context: Context?, spName: String?, key: String?, value: Float): Boolean {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        val editor = settings.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    /**
     * @param context
     * @param spName
     * @return
     */
    fun getEditor(context: Context?, spName: String?): SharedPreferences.Editor {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        return settings.edit()
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a float
     * @see .getFloat
     */
    fun getFloat(context: Context?, spName: String?, key: String?): Float {
        return getFloat(context, spName, key, -1f)
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a float
     */
    fun getFloat(context: Context?, spName: String?, key: String?, defaultValue: Float): Float {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        return settings.getFloat(key, defaultValue)
    }

    /**
     * put boolean preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putBoolean(context: Context?, spName: String?, key: String?, value: Boolean): Boolean {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        val editor = settings.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    /**
     * get boolean preferences, default is false
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     * name that is not a boolean
     * @see .getBoolean
     */
    fun getBoolean(context: Context?, spName: String?, key: String?): Boolean {
        return getBoolean(context, spName, key, false)
    }

    /**
     * get boolean preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a boolean
     */
    fun getBoolean(
        context: Context?,
        spName: String?,
        key: String?,
        defaultValue: Boolean
    ): Boolean {
        val settings: SharedPreferences = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        )
        return settings.getBoolean(key, defaultValue)
    }
}