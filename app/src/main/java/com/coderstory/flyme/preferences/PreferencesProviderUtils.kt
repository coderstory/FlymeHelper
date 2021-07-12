package com.coderstory.flyme.preferencesimport

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Base64

android.widget.NumberPicker
import android.view.ViewGroup
import android.widget.EditText
import kotlin.jvm.JvmOverloads
import com.coderstory.flyme.refreshView.BaseRefreshView
import android.view.animation.Animation
import com.coderstory.flyme.view.PullToRefreshView
import com.coderstory.flyme.refreshView.SunRefreshView
import android.view.View.MeasureSpec
import android.view.MotionEvent
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import android.content.res.TypedArray
import com.coderstory.flyme.R
import android.view.ViewConfiguration
import com.coderstory.flyme.tools.Cpp
import com.coderstory.flyme.tools.Misc
import com.coderstory.flyme.tools.SharedHelper
import android.os.Bundle
import android.app.Activity
import com.coderstory.flyme.tools.hostshelper.FileHelper
import android.widget.Toast
import kotlin.Throws
import android.text.TextUtils
import android.os.Parcelable
import com.coderstory.flyme.tools.licensesdialog.licenses.License
import android.os.Parcel
import android.os.Parcelable.Creator
import com.coderstory.flyme.tools.licensesdialog.model.Notice
import com.coderstory.flyme.tools.licensesdialog.model.Notices
import com.coderstory.flyme.tools.licensesdialog.LicensesDialog
import android.webkit.WebView
import com.coderstory.flyme.tools.licensesdialog.licenses.ApacheSoftwareLicense20
import android.webkit.WebChromeClient
import android.webkit.WebView.HitTestResult
import com.coderstory.flyme.tools.licensesdialog.NoticesXmlParser
import com.coderstory.flyme.tools.licensesdialog.NoticesHtmlBuilder
import com.coderstory.flyme.tools.licensesdialog.LicenseResolver
import com.coderstory.flyme.tools.licensesdialog.licenses.GnuGeneralPublicLicense20
import org.xmlpull.v1.XmlPullParser
import android.util.Xml
import org.xmlpull.v1.XmlPullParserException
import com.coderstory.flyme.tools.licensesdialog.LicensesDialogFragment
import android.os.Build
import androidx.annotation.RawRes
import androidx.annotation.StyleRes
import androidx.annotation.ColorRes
import androidx.annotation.ColorInt
import com.coderstory.flyme.fragment.base.BaseFragment
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import com.coderstory.flyme.tools.AppSignCheck
import com.coderstory.flyme.tools.CrashHandler
import android.os.Looper
import android.annotation.SuppressLint
import com.coderstory.flyme.preferences.PreferencesProviderUtils
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XC_MethodHook
import com.coderstory.flyme.tools.XposedHelper
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import com.google.gson.Gson
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import com.google.android.material.snackbar.Snackbar
import com.coderstory.flyme.tools.SnackBarUtils
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import com.itsnows.upgrade.UpgradeManager
import com.itsnows.upgrade.model.bean.UpgradeOptions
import android.graphics.BitmapFactory
import android.os.Environment
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import com.coderstory.flyme.patchModule.FlymeRoot
import com.coderstory.flyme.patchModule.FlymeHome
import com.coderstory.flyme.patchModule.Others
import com.coderstory.flyme.patchModule.SystemUi
import com.coderstory.flyme.patchModule.IsEnable
import com.coderstory.flyme.patchModule.HideApp
import com.coderstory.flyme.patchModule.ThemePatcher
import com.coderstory.flyme.patchModule.FuckAd
import com.coderstory.flyme.patchModule.corepatch.CorePatchForR
import com.coderstory.flyme.patchModule.corepatch.CorePatchForQ
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import android.graphics.drawable.Drawable
import com.coderstory.flyme.adapter.AppInfo
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.coderstory.flyme.activity.base.BaseActivity
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.app.ProgressDialog
import com.coderstory.flyme.activity.MainActivity
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.AppSettingsDialog
import com.coderstory.flyme.R.id
import com.coderstory.flyme.update.updgradeService
import per.goweii.anylayer.AnyLayer
import com.coderstory.flyme.fragment.OthersFragment
import com.coderstory.flyme.fragment.SettingsFragment
import com.coderstory.flyme.fragment.CleanFragment
import com.coderstory.flyme.fragment.DisbaleAppFragment
import com.coderstory.flyme.activity.AboutActivity
import com.coderstory.flyme.fragment.HideAppFragment
import com.coderstory.flyme.fragment.BlogFragment
import com.coderstory.flyme.fragment.UpgradeFragment
import com.coderstory.flyme.fragment.SystemUIFragment
import com.coderstory.flyme.fragment.HostsFragment
import com.coderstory.flyme.fragment.AccountFragment
import com.coderstory.flyme.fragment.XposedFragment
import com.coderstory.flyme.fragment.CorePatchFragment
import androidx.core.view.GravityCompat
import com.coderstory.flyme.activity.ToolbarActivity
import com.coderstory.flyme.fragment.AboutFragment
import android.view.WindowManager
import android.os.AsyncTask
import com.coderstory.flyme.activity.SplashActivity
import com.google.android.material.appbar.AppBarLayout
import android.view.MenuInflater
import android.text.method.ScrollingMovementMethod
import com.coderstory.flyme.fragment.CleanFragment.CacheSize
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import per.goweii.anylayer.DialogLayer
import android.widget.LinearLayout
import android.widget.NumberPicker.OnValueChangeListener
import android.text.TextWatcher
import android.text.Editable
import android.graphics.drawable.ColorDrawable
import android.content.res.Resources.NotFoundException
import android.text.InputFilter.LengthFilter
import android.text.method.DigitsKeyListener
import com.coderstory.flyme.adapter.AppInfoAdapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.widget.ProgressBar
import com.coderstory.flyme.fragment.WebViewFragment.MyWebViewClient
import com.coderstory.flyme.fragment.WebViewFragment.MyWebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.content.pm.ApplicationInfo
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.coderstory.flyme.tools.ReturnConstant
import de.robv.android.xposed.XC_MethodReplacement
import com.coderstory.flyme.xposed.IModule
import android.appwidget.AppWidgetProviderInfo
import android.view.Gravity
import android.os.Vibrator
import android.app.AndroidAppHelper
import android.database.sqlite.SQLiteOpenHelper
import com.coderstory.flyme.preferences.PreferencesUtils
import com.coderstory.flyme.preferences.PreferencesProvider
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.view.animation.LinearInterpolator
import android.graphics.PixelFormat
import android.graphics.ColorFilter

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
    fun putString(context: Context, spName: String?, key: String?, value: String?): Boolean {
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.STRING_CONTENT_URI_CODE, spName, key, value)
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
    fun remove(context: Context, spName: String?, key: String?): Boolean {
        try {
            val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.DELETE_CONTENT_URI_CODE, spName, key, null)
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
    fun getString(context: Context?, spName: String?, key: String?): String {
        return PreferencesProviderUtils.getString(context, spName, key, "")
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
    fun getString(context: Context, spName: String?, key: String?, defaultValue: String?): String? {
        var result = defaultValue
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.STRING_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = String(Base64.decode(cursor.getString(cursor.getColumnIndex(PreferencesProvider.Companion.COLUMNNAME)).toByteArray(), Base64.DEFAULT))
        }
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
    fun putInt(context: Context, spName: String?, key: String?, value: Int): Boolean {
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE, spName, key, value)
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
    fun getInt(context: Context?, spName: String?, key: String?): Int {
        return PreferencesProviderUtils.getInt(context, spName, key, -1)
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
    fun getInt(context: Context, spName: String?, key: String?, defaultValue: Int): Int {
        var result = defaultValue
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = cursor.getInt(cursor.getColumnIndex(PreferencesProvider.Companion.COLUMNNAME))
        }
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
    fun putLong(context: Context, spName: String?, key: String?, value: Long): Boolean {
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.LONG_CONTENT_URI_CODE, spName, key, value)
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
    fun getLong(context: Context?, spName: String?, key: String?): Long {
        return PreferencesProviderUtils.getLong(context, spName, key, -1)
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
    fun getLong(context: Context, spName: String?, key: String?, defaultValue: Long): Long {
        var result = defaultValue
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.LONG_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = cursor.getLong(cursor.getColumnIndex(PreferencesProvider.Companion.COLUMNNAME))
        }
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
    fun putFloat(context: Context, spName: String?, key: String?, value: Float): Boolean {
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE, spName, key, value)
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
    fun getFloat(context: Context?, spName: String?, key: String?): Float {
        return PreferencesProviderUtils.getFloat(context, spName, key, -1f)
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
    fun getFloat(context: Context, spName: String?, key: String?, defaultValue: Float): Float {
        var result = defaultValue
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = cursor.getFloat(cursor.getColumnIndex(PreferencesProvider.Companion.COLUMNNAME))
        }
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
    fun putBoolean(context: Context, spName: String?, key: String?, value: Boolean): Boolean {
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE, spName, key, value)
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
    fun getBoolean(context: Context?, spName: String?, key: String?): Boolean {
        return PreferencesProviderUtils.getBoolean(context, spName, key, false)
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
    fun getBoolean(context: Context, spName: String?, key: String?, defaultValue: Boolean): Boolean {
        var result = defaultValue
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE, spName, key, defaultValue)
        val cr = context.contentResolver
        val cursor = cr.query(uri, null, null, null, null) ?: return result
        if (cursor.moveToNext()) {
            result = String(Base64.decode(cursor.getString(cursor.getColumnIndex(PreferencesProvider.Companion.COLUMNNAME)).toByteArray(), Base64.DEFAULT)) == "true"
        }
        return result
    }

    /**
     * @param context
     * @param spName
     * @param datas
     * @return
     */
    fun put(context: Context, spName: String?, datas: ContentValues?): Boolean {
        val uri = PreferencesProviderUtils.buildUri(PreferencesProvider.Companion.PUTS_CONTENT_URI_CODE, spName, null, null)
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
    private fun buildUri(code: Int, spName: String, key: String, value: Any): Uri? {
        val authorities = "com.coderstory.flyme.preferencesProvider"
        var uri: Uri? = null
        when (code) {
            PreferencesProvider.Companion.STRING_CONTENT_URI_CODE -> uri = Uri
                    .parse("content://$authorities/string/$spName/$key/$value")
            PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE -> uri = Uri
                    .parse("content://$authorities/integer/$spName/$key/$value")
            PreferencesProvider.Companion.LONG_CONTENT_URI_CODE -> uri = Uri
                    .parse("content://$authorities/long/$spName/$key/$value")
            PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE -> uri = Uri
                    .parse("content://$authorities/float/$spName/$key/$value")
            PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE -> uri = Uri
                    .parse("content://$authorities/boolean/$spName/$key/$value")
            PreferencesProvider.Companion.DELETE_CONTENT_URI_CODE -> uri = Uri
                    .parse("content://$authorities/delete/$spName/$key")
            PreferencesProvider.Companion.PUTS_CONTENT_URI_CODE -> uri = Uri
                    .parse("content://$authorities/puts")
            else -> {
            }
        }
        return uri
    }
}