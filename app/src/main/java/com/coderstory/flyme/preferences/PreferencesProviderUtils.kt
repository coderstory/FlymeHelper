package com.coderstory.flyme.preferences


import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Base64

/**
 * @Description: PreferencesProviderUtils
 * @author: zhangliangming
 * @date: 2018-04-29 19:07
 */
object PreferencesProviderUtils {
    /**
     * put string preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putString(context: Context, spName: String, key: String, value: String?): Boolean {
        val uri = buildUri(PreferencesProvider.STRING_CONTENT_URI_CODE, spName, key, value)
        val cr = context.contentResolver
        try {
            val values = ContentValues()
            values.put(key, value)
            cr.insert(uri, values)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 移除
     *
     * @param context
     * @param spName
     * @param key
     * @return
     */
    fun remove(context: Context, spName: String, key: String): Boolean {
        try {
            val uri = buildUri(PreferencesProvider.DELETE_CONTENT_URI_CODE, spName, key, null)
            val cr = context.contentResolver
            cr.delete(uri, null, null)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
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
        var result = defaultValue
        val uri = buildUri(PreferencesProvider.STRING_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = String(
                Base64.decode(
                    cursor.getString(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME))
                        .toByteArray(), Base64.DEFAULT
                )
            )
        }
        cursor.close()
        return result
    }

    /**
     * put int preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putInt(context: Context, spName: String, key: String, value: Int): Boolean {
        val uri = buildUri(PreferencesProvider.INTEGER_CONTENT_URI_CODE, spName, key, value)
        val cr = context.contentResolver
        try {
            val values = ContentValues()
            values.put(key, value)
            cr.insert(uri, values)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
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
    fun getInt(context: Context, spName: String, key: String): Int {
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
    fun getInt(context: Context, spName: String, key: String, defaultValue: Int): Int {
        var result = defaultValue
        val uri = buildUri(PreferencesProvider.INTEGER_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = cursor.getInt(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME))
        }
        cursor.close()
        return result
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putLong(context: Context, spName: String, key: String, value: Long): Boolean {
        val uri = buildUri(PreferencesProvider.LONG_CONTENT_URI_CODE, spName, key, value)
        val cr = context.contentResolver
        try {
            val values = ContentValues()
            values.put(key, value)
            cr.insert(uri, values)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
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
    fun getLong(context: Context, spName: String, key: String): Long {
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
    fun getLong(context: Context, spName: String, key: String, defaultValue: Long): Long {
        var result = defaultValue
        val uri = buildUri(PreferencesProvider.LONG_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = cursor.getLong(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME))
        }
        cursor.close()
        return result
    }

    /**
     * put float preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putFloat(context: Context, spName: String, key: String, value: Float): Boolean {
        val uri = buildUri(PreferencesProvider.FLOAT_CONTENT_URI_CODE, spName, key, value)
        val cr = context.contentResolver
        try {
            val values = ContentValues()
            values.put(key, value)
            cr.insert(uri, values)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
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
    fun getFloat(context: Context, spName: String, key: String): Float {
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
    fun getFloat(context: Context, spName: String, key: String, defaultValue: Float): Float {
        var result = defaultValue
        val uri = buildUri(PreferencesProvider.FLOAT_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = cursor.getFloat(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME))
        }
        cursor.close()
        return result
    }

    /**
     * put boolean preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    fun putBoolean(context: Context, spName: String, key: String, value: Boolean): Boolean {
        val uri = buildUri(PreferencesProvider.BOOLEAN_CONTENT_URI_CODE, spName, key, value)
        val cr = context.contentResolver
        try {
            val values = ContentValues()
            values.put(key, value)
            cr.insert(uri, values)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
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
    fun getBoolean(context: Context, spName: String, key: String): Boolean {
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
    fun getBoolean(context: Context, spName: String, key: String, defaultValue: Boolean): Boolean {
        var result = defaultValue
        val uri = buildUri(PreferencesProvider.BOOLEAN_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = String(
                Base64.decode(
                    cursor.getString(cursor.getColumnIndex(PreferencesProvider.COLUMNNAME))
                        .toByteArray(), Base64.DEFAULT
                )
            ) == "true"
        }
        cursor.close()
        return result
    }

    /**
     * @param context
     * @param spName
     * @param datas
     * @return
     */
    fun put(context: Context, spName: String, datas: ContentValues?): Boolean {
        val uri = buildUri(PreferencesProvider.PUTS_CONTENT_URI_CODE, spName, null, null)
        val cr = context.contentResolver
        try {
            cr.insert(uri, datas)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * @param code
     * @param key
     * @param value
     * @return
     */
    private fun buildUri(code: Int, spName: String, key: String?, value: Any?): Uri {
        val authorities = "com.coderstory.flyme.PreferencesProvider"
        var uri: Uri? = null
        when (code) {
            PreferencesProvider.STRING_CONTENT_URI_CODE -> uri = Uri
                .parse("content://$authorities/string/$spName/$key/$value")

            PreferencesProvider.INTEGER_CONTENT_URI_CODE -> uri = Uri
                .parse("content://$authorities/integer/$spName/$key/$value")

            PreferencesProvider.LONG_CONTENT_URI_CODE -> uri = Uri
                .parse("content://$authorities/long/$spName/$key/$value")

            PreferencesProvider.FLOAT_CONTENT_URI_CODE -> uri = Uri
                .parse("content://$authorities/float/$spName/$key/$value")

            PreferencesProvider.BOOLEAN_CONTENT_URI_CODE -> uri = Uri
                .parse("content://$authorities/boolean/$spName/$key/$value")

            PreferencesProvider.DELETE_CONTENT_URI_CODE -> uri = Uri
                .parse("content://$authorities/delete/$spName/$key")

            PreferencesProvider.PUTS_CONTENT_URI_CODE -> uri = Uri
                .parse("content://$authorities/puts")

            else -> {
            }
        }
        return uri!!
    }
}