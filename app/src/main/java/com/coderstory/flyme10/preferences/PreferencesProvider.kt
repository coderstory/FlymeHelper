package com.coderstory.flyme10.preferences

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import com.coderstory.flyme10.tools.Misc
import com.coderstory.flyme10.tools.Utils

/**
 * @Description: ContentProvider
 * @author: zhangliangming
 * @date: 2018-04-29 16:39
 */
abstract class PreferencesProvider : ContentProvider() {
    /**
     * string
     */
    private val mStringPath = "string/*/*/"

    /**
     * int
     */
    private val mIntegerPath = "integer/*/*/"

    /**
     * long
     */
    private val mLongPath = "long/*/*/"

    /**
     * float
     */
    private val mFloatPath = "float/*/*/"

    /**
     * boolean
     */
    private val mBooleanPath = "boolean/*/*/"

    /**
     *
     */
    private val mDeletePath = "delete/*/*/"

    /**
     *
     */
    private val mPutsPath = "puts"
    private var mUriMatcher: UriMatcher? = null
    abstract val authorities: String
    override fun onCreate(): Boolean {
        val authorities = authorities
        //保存authorities
        PreferencesUtils.putString(context, AUTHORITIES_SPNAME, AUTHORITIES_KEY, authorities)
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher!!.addURI(authorities, mStringPath, STRING_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mStringPath*/", STRING_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mIntegerPath, INTEGER_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mIntegerPath*/", INTEGER_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mLongPath, LONG_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mLongPath*/", LONG_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mFloatPath, FLOAT_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mFloatPath*/", FLOAT_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mBooleanPath, BOOLEAN_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mBooleanPath*/", BOOLEAN_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mDeletePath, DELETE_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mPutsPath, PUTS_CONTENT_URI_CODE)
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val model = getModel(uri) ?: return null
        val code = mUriMatcher!!.match(uri)
        return buildCursor(context!!, model, code)
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        getModel(uri) ?: return null
        val code = mUriMatcher!!.match(uri)
        if (code == STRING_CONTENT_URI_CODE || code == INTEGER_CONTENT_URI_CODE || code == LONG_CONTENT_URI_CODE || code == FLOAT_CONTENT_URI_CODE || code == BOOLEAN_CONTENT_URI_CODE || code == PUTS_CONTENT_URI_CODE) {
            insert(context, values)
        }
        return uri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val model = getModel(uri) ?: return -1
        val code = mUriMatcher!!.match(uri)
        if (code == STRING_CONTENT_URI_CODE || code == INTEGER_CONTENT_URI_CODE || code == LONG_CONTENT_URI_CODE || code == FLOAT_CONTENT_URI_CODE || code == BOOLEAN_CONTENT_URI_CODE) {
            delete(context, model)
        }
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        getModel(uri) ?: return -1
        val code = mUriMatcher!!.match(uri)
        if (code == STRING_CONTENT_URI_CODE || code == INTEGER_CONTENT_URI_CODE || code == LONG_CONTENT_URI_CODE || code == FLOAT_CONTENT_URI_CODE || code == BOOLEAN_CONTENT_URI_CODE) {
            insert(context, values)
        }
        return 0
    }

    /**
     * 删除
     *
     * @param context
     * @param model
     */
    private fun delete(context: Context?, model: Model) {
        val editor = PreferencesUtils.getEditor(context, model.spName)
        editor.remove(model.key)
        editor.commit()
    }

    /**
     * 插入数据
     *
     * @param context
     * @param values
     */
    private fun insert(context: Context?, values: ContentValues?) {
        //Log.e("Xposed", "Model " + JSON.toJSONString(model));
        //Log.e("Xposed", "ContentValues " + JSON.toJSONString(values));
        val editor: SharedPreferences.Editor = Utils.getMySharedPreferences(
            context,
            "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/",
            Misc.SharedPreferencesName
        ).edit()
        val keys = values!!.keySet()
        for (key in keys) {
            val value = values[key]
            if (value is Int) {
                editor.putInt(key, (value.toString() + "").toInt())
            } else if (value is Long) {
                editor.putLong(key, (value.toString() + "").toLong())
            } else if (value is Float) {
                editor.putFloat(key, (value.toString() + "").toFloat())
            } else if (value is Boolean) {
                editor.putBoolean(key, java.lang.Boolean.valueOf(value.toString() + ""))
            } else {
                editor.putString(
                    key,
                    if (value == null) "" else String(
                        Base64.decode(
                            value as String,
                            Base64.DEFAULT
                        )
                    )
                )
            }
        }
        editor.apply()
    }

    /**
     * 从sp中获取数据
     *
     * @return
     */
    private fun buildCursor(context: Context, model: Model, code: Int): Cursor? {
        var value: Any? = null
        var defValue = model.defValue
        when (code) {
            STRING_CONTENT_URI_CODE -> value =
                PreferencesUtils.getString(context, model.spName, model.key, defValue.toString())

            INTEGER_CONTENT_URI_CODE -> {
                if (!TextUtils.isDigitsOnly(defValue.toString() + "")) {
                    defValue = -1
                }
                value = PreferencesUtils.getInt(
                    context,
                    model.spName,
                    model.key,
                    (defValue.toString() + "").toInt()
                )
            }

            LONG_CONTENT_URI_CODE -> {
                if (!TextUtils.isDigitsOnly(defValue.toString() + "")) {
                    defValue = -1
                }
                value = PreferencesUtils.getLong(
                    context,
                    model.spName,
                    model.key,
                    (defValue.toString() + "").toLong()
                )
            }

            FLOAT_CONTENT_URI_CODE -> {
                value = PreferencesUtils.getFloat(
                    context,
                    model.spName,
                    model.key,
                    (defValue.toString() + "").toFloat()
                )
            }

            BOOLEAN_CONTENT_URI_CODE -> value = PreferencesUtils.getBoolean(
                context,
                model.spName,
                model.key,
                java.lang.Boolean.valueOf(defValue.toString() + "")
            ).toString() + ""

            else -> {
            }
        }
        if (value == null) return null
        //
        if (value is String) {
            value = Base64.encodeToString(value.toByteArray(), Base64.DEFAULT)
        }
        val columnNames = arrayOf(COLUMNNAME)
        val cursor = MatrixCursor(columnNames)
        val values = arrayOf(value)
        cursor.addRow(values)
        return cursor
    }

    /**
     * 从uri中获取spname和key
     *
     * @param uri
     * @return
     */
    private fun getModel(uri: Uri): Model? {
        try {
            val model = Model()
            model.spName = (uri.pathSegments[1])
            if (uri.pathSegments.size > 2) {
                model.key = (uri.pathSegments[2])
            }
            if (uri.pathSegments.size > 3) {
                model.defValue = (uri.pathSegments[3])
            }
            return model
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     *
     */
    private class Model {
        var spName: String = ""
        var key: String = ""
        var defValue: Any = ""
    }

    companion object {
        const val STRING_CONTENT_URI_CODE = 100
        const val INTEGER_CONTENT_URI_CODE = 101
        const val LONG_CONTENT_URI_CODE = 102
        const val FLOAT_CONTENT_URI_CODE = 104
        const val BOOLEAN_CONTENT_URI_CODE = 105
        const val DELETE_CONTENT_URI_CODE = 106
        const val PUTS_CONTENT_URI_CODE = 107

        /**
         * 表列名
         */
        var COLUMNNAME = "SPCOLUMNNAME"

        /**
         * authorities key
         */
        var AUTHORITIES_KEY = "authorities_key"

        /**
         * authorities_spname
         */
        var AUTHORITIES_SPNAME = "UserSettings"
    }
}