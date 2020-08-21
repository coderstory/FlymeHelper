package com.coderstory.flyme.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.coderstory.flyme.utils.Misc;
import com.coderstory.flyme.utils.Utils;

import static com.coderstory.flyme.utils.Misc.ApplicationName;

/**
 * PreferencesUtils, easy to get or put data
 * <ul>
 * <strong>Preference Name</strong>
 * <li>you can change preference name by {@link #}</li>
 * </ul>
 * <ul>
 * <strong>Put Value</strong>
 * <li>put string {@link #putString(Context, String, String, String)}</li>
 * <li>put int {@link #putInt(Context, String, String, int)}</li>
 * <li>put long {@link #putLong(Context, String, String, long)}</li>
 * <li>put float {@link #putFloat(Context, String, String, float)}</li>
 * <li>put boolean {@link #putBoolean(Context, String, String, boolean)}</li>
 * </ul>
 * <ul>
 * <strong>Get Value</strong>
 * <li>get string {@link #getString(Context, String, String)}, {@link #getString(Context, String, String)}</li>
 * <li>get int {@link #getInt(Context, String, String)}, {@link #getInt(Context, String, String, int)}</li>
 * <li>get long {@link #getLong(Context, String, String)}, {@link #getLong(Context, String, String, long)}</li>
 * <li>get float {@link #getFloat(Context, String, String)}, {@link #getFloat(Context, String, String, float)}</li>
 * <li>get boolean {@link #getBoolean(Context, String, String)}, {@link #getBoolean(Context, String, String, boolean)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-3-6
 */
public class PreferencesUtils {
    /**
     * put string preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putString(Context context, String spName, String key, String value) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        SharedPreferences.Editor editor = settings.edit();
        Log.e(key, value);
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean remove(Context context, String spName, String key) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     * name that is not a string
     * @see #getString(Context, String, String)
     */
    public static String getString(Context context, String spName, String key) {
        return getString(context, spName, key, null);
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
    public static String getString(Context context, String spName, String key, String defaultValue) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putInt(Context context, String spName, String key, int value) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a int
     * @see #getInt(Context, String, String, int)
     */
    public static int getInt(Context context, String spName, String key) {
        return getInt(context, spName, key, -1);
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
    public static int getInt(Context context, String spName, String key, int defaultValue) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putLong(Context context, String spName, String key, long value) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a long
     * @see #getLong(Context, String, String, long)
     */
    public static long getLong(Context context, String spName, String key) {
        return getLong(context, spName, key, -1);
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
    public static long getLong(Context context, String spName, String key, long defaultValue) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putFloat(Context context, String spName, String key, float value) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     *
     * @param context
     * @param spName
     * @return
     */
    public static SharedPreferences.Editor getEditor(Context context, String spName){
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        SharedPreferences.Editor editor = settings.edit();
        return editor;
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a float
     * @see #getFloat(Context, String, String, float)
     */
    public static float getFloat(Context context, String spName, String key) {
        return getFloat(context, spName, key, -1);
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
    public static float getFloat(Context context, String spName, String key, float defaultValue) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putBoolean(Context context, String spName, String key, boolean value) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     * name that is not a boolean
     * @see #getBoolean(Context, String, String, boolean)
     */
    public static boolean getBoolean(Context context, String spName, String key) {
        return getBoolean(context, spName, key, false);
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
    public static boolean getBoolean(Context context, String spName, String key, boolean defaultValue) {
        SharedPreferences settings = Utils.getMySharedPreferences(context, "/data/user_de/0/" + ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName);
        return settings.getBoolean(key, defaultValue);
    }

}
