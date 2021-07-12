package com.coderstory.flyme.preferences;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * @Description: PreferencesProviderUtils
 * @author: zhangliangming
 * @date: 2018-04-29 19:07
 **/
public class PreferencesProviderUtils {

    /**
     * put string preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putString(Context context, String spName, String key, String value) {
        Uri uri = buildUri(PreferencesProvider.STRING_CONTENT_URI_CODE, spName, key, value);
        ContentResolver cr = context.getContentResolver();
        try {
            ContentValues values = new ContentValues();
            values.put(key, value);

            cr.insert(uri, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 移除
     *
     * @param context
     * @param spName
     * @param key
     * @return
     */
    public static boolean remove(Context context, String spName, String key) {
        try {
            Uri uri = buildUri(PreferencesProvider.DELETE_CONTENT_URI_CODE, spName, key, null);
            ContentResolver cr = context.getContentResolver();

            cr.delete(uri, null, null);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

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
        return getString(context, spName, key, "");
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
        String result = defaultValue;
        Uri uri = buildUri(PreferencesProvider.STRING_CONTENT_URI_CODE, spName, key, defaultValue);
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor == null) return result;
        if (cursor.moveToNext()) {
            result = new String(android.util.Base64.decode(cursor.getString(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME)).getBytes(), android.util.Base64.DEFAULT));
        }

        return result;
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
        Uri uri = buildUri(PreferencesProvider.INTEGER_CONTENT_URI_CODE, spName, key, value);
        ContentResolver cr = context.getContentResolver();
        try {
            ContentValues values = new ContentValues();
            values.put(key, value);

            cr.insert(uri, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        int result = defaultValue;
        Uri uri = buildUri(PreferencesProvider.INTEGER_CONTENT_URI_CODE, spName, key, defaultValue);
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor == null) return result;

        if (cursor.moveToNext()) {
            result = cursor.getInt(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME));

        }
        return result;
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
        Uri uri = buildUri(PreferencesProvider.LONG_CONTENT_URI_CODE, spName, key, value);
        ContentResolver cr = context.getContentResolver();
        try {
            ContentValues values = new ContentValues();
            values.put(key, value);

            cr.insert(uri, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        long result = defaultValue;
        Uri uri = buildUri(PreferencesProvider.LONG_CONTENT_URI_CODE, spName, key, defaultValue);
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor == null) return result;

        if (cursor.moveToNext()) {
            result = cursor.getLong(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME));
        }

        return result;
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
        Uri uri = buildUri(PreferencesProvider.FLOAT_CONTENT_URI_CODE, spName, key, value);
        ContentResolver cr = context.getContentResolver();
        try {
            ContentValues values = new ContentValues();
            values.put(key, value);

            cr.insert(uri, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        float result = defaultValue;
        Uri uri = buildUri(PreferencesProvider.FLOAT_CONTENT_URI_CODE, spName, key, defaultValue);
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor == null) return result;

        if (cursor.moveToNext()) {
            result = cursor.getFloat(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME));
        }

        return result;
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
        Uri uri = buildUri(PreferencesProvider.BOOLEAN_CONTENT_URI_CODE, spName, key, value);
        ContentResolver cr = context.getContentResolver();
        try {
            ContentValues values = new ContentValues();
            values.put(key, value);

            cr.insert(uri, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        boolean result = defaultValue;
        Uri uri = buildUri(PreferencesProvider.BOOLEAN_CONTENT_URI_CODE, spName, key, defaultValue);
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor == null) return result;

        if (cursor.moveToNext()) {
            result = new String(android.util.Base64.decode(cursor.getString(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME)).getBytes(), android.util.Base64.DEFAULT)).equals("true");
        }

        return result;
    }

    /**
     * @param context
     * @param spName
     * @param datas
     * @return
     */
    public static boolean put(Context context, String spName, ContentValues datas) {
        Uri uri = buildUri(PreferencesProvider.PUTS_CONTENT_URI_CODE, spName, null, null);
        ContentResolver cr = context.getContentResolver();
        try {

            cr.insert(uri, datas);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param code
     * @param key
     * @param value
     * @return
     */
    private static Uri buildUri(int code, String spName, String key, Object value) {
        String authorities = "com.coderstory.flyme.preferencesProvider";
        Uri uri = null;

        switch (code) {
            case PreferencesProvider.STRING_CONTENT_URI_CODE:
                uri = Uri
                        .parse("content://" + authorities + "/" + "string/" + spName + "/" + key + "/" + value);

                break;
            case PreferencesProvider.INTEGER_CONTENT_URI_CODE:

                uri = Uri
                        .parse("content://" + authorities + "/" + "integer/" + spName + "/" + key + "/" + value);


                break;
            case PreferencesProvider.LONG_CONTENT_URI_CODE:

                uri = Uri
                        .parse("content://" + authorities + "/" + "long/" + spName + "/" + key + "/" + value);


                break;
            case PreferencesProvider.FLOAT_CONTENT_URI_CODE:

                uri = Uri
                        .parse("content://" + authorities + "/" + "float/" + spName + "/" + key + "/" + value);


                break;
            case PreferencesProvider.BOOLEAN_CONTENT_URI_CODE:

                uri = Uri
                        .parse("content://" + authorities + "/" + "boolean/" + spName + "/" + key + "/" + value);


                break;

            case PreferencesProvider.DELETE_CONTENT_URI_CODE:

                uri = Uri
                        .parse("content://" + authorities + "/" + "delete/" + spName + "/" + key);


                break;

            case PreferencesProvider.PUTS_CONTENT_URI_CODE:

                uri = Uri
                        .parse("content://" + authorities + "/" + "puts");


                break;

            default:
                break;
        }
        return uri;
    }
}
