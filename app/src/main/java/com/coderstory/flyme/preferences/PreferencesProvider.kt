package com.coderstory.flyme.preferencesimport

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import com.coderstory.flyme.preferences.PreferencesUtils
import com.coderstory.flyme.tools.*

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
import android.os.Looper
import android.annotation.SuppressLint
import com.coderstory.flyme.preferences.PreferencesProviderUtils
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import com.google.gson.Gson
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import com.google.android.material.snackbar.Snackbar
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
        PreferencesUtils.putString(context, PreferencesProvider.Companion.AUTHORITIES_SPNAME, PreferencesProvider.Companion.AUTHORITIES_KEY, authorities)
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher!!.addURI(authorities, mStringPath, PreferencesProvider.Companion.STRING_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mStringPath*/", PreferencesProvider.Companion.STRING_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mIntegerPath, PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mIntegerPath*/", PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mLongPath, PreferencesProvider.Companion.LONG_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mLongPath*/", PreferencesProvider.Companion.LONG_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mFloatPath, PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mFloatPath*/", PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mBooleanPath, PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, "$mBooleanPath*/", PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mDeletePath, PreferencesProvider.Companion.DELETE_CONTENT_URI_CODE)
        mUriMatcher!!.addURI(authorities, mPutsPath, PreferencesProvider.Companion.PUTS_CONTENT_URI_CODE)
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val model = getModel(uri) ?: return null
        val code = mUriMatcher!!.match(uri)
        return buildCursor(context, model, code)
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val model = getModel(uri) ?: return null
        val code = mUriMatcher!!.match(uri)
        if (code == PreferencesProvider.Companion.STRING_CONTENT_URI_CODE || code == PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE || code == PreferencesProvider.Companion.LONG_CONTENT_URI_CODE || code == PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE || code == PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE || code == PreferencesProvider.Companion.PUTS_CONTENT_URI_CODE) {
            insert(context, values, model)
        }
        return uri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val model = getModel(uri) ?: return -1
        val code = mUriMatcher!!.match(uri)
        if (code == PreferencesProvider.Companion.STRING_CONTENT_URI_CODE || code == PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE || code == PreferencesProvider.Companion.LONG_CONTENT_URI_CODE || code == PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE || code == PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE) {
            delete(context, model)
        }
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val model = getModel(uri) ?: return -1
        val code = mUriMatcher!!.match(uri)
        if (code == PreferencesProvider.Companion.STRING_CONTENT_URI_CODE || code == PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE || code == PreferencesProvider.Companion.LONG_CONTENT_URI_CODE || code == PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE || code == PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE) {
            insert(context, values, model)
        }
        return 0
    }

    /**
     * 删除
     *
     * @param context
     * @param model
     */
    private fun delete(context: Context?, model: PreferencesProvider.Model) {
        val editor = PreferencesUtils.getEditor(context, model.getSpName())
        editor.remove(model.getKey())
        editor.commit()
    }

    /**
     * 插入数据
     *
     * @param context
     * @param values
     * @param model
     */
    private fun insert(context: Context?, values: ContentValues?, model: PreferencesProvider.Model) {
        //Log.e("Xposed", "Model " + JSON.toJSONString(model));
        //Log.e("Xposed", "ContentValues " + JSON.toJSONString(values));
        val editor: SharedPreferences.Editor = Utils.Companion.getMySharedPreferences(context, "/data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/", Misc.SharedPreferencesName).edit()
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
                editor.putString(key, if (value == null) "" else String(Base64.decode(value as String, Base64.DEFAULT)))
            }
        }
        editor.commit()
    }

    /**
     * 从sp中获取数据
     *
     * @return
     */
    private fun buildCursor(context: Context?, model: PreferencesProvider.Model, code: Int): Cursor? {
        var value: Any? = null
        var defValue = model.getDefValue()
        when (code) {
            PreferencesProvider.Companion.STRING_CONTENT_URI_CODE -> value = if (defValue == null) {
                PreferencesUtils.getString(context, model.getSpName(), model.getKey())
            } else {
                PreferencesUtils.getString(context, model.getSpName(), model.getKey(), defValue.toString())
            }
            PreferencesProvider.Companion.INTEGER_CONTENT_URI_CODE -> if (defValue == null) {
                value = PreferencesUtils.getInt(context, model.getSpName(), model.getKey())
            } else {
                if (!TextUtils.isDigitsOnly(defValue.toString() + "")) {
                    defValue = -1
                }
                value = PreferencesUtils.getInt(context, model.getSpName(), model.getKey(), (defValue.toString() + "").toInt())
            }
            PreferencesProvider.Companion.LONG_CONTENT_URI_CODE -> if (defValue == null) {
                value = PreferencesUtils.getLong(context, model.getSpName(), model.getKey())
            } else {
                if (!TextUtils.isDigitsOnly(defValue.toString() + "")) {
                    defValue = -1
                }
                value = PreferencesUtils.getLong(context, model.getSpName(), model.getKey(), (defValue.toString() + "").toLong())
            }
            PreferencesProvider.Companion.FLOAT_CONTENT_URI_CODE -> value = if (defValue == null) {
                PreferencesUtils.getFloat(context, model.getSpName(), model.getKey())
            } else {
                PreferencesUtils.getFloat(context, model.getSpName(), model.getKey(), (defValue.toString() + "").toFloat())
            }
            PreferencesProvider.Companion.BOOLEAN_CONTENT_URI_CODE -> value = if (defValue == null) {
                PreferencesUtils.getBoolean(context, model.getSpName(), model.getKey()).toString() + ""
            } else {
                PreferencesUtils.getBoolean(context, model.getSpName(), model.getKey(), java.lang.Boolean.valueOf(defValue.toString() + "")).toString() + ""
            }
            else -> {
            }
        }
        if (value == null) return null
        //
        if (value is String) {
            value = Base64.encodeToString(value.toByteArray(), Base64.DEFAULT)
        }
        val columnNames = arrayOf<String>(PreferencesProvider.Companion.COLUMNNAME)
        val cursor = MatrixCursor(columnNames)
        val values = arrayOf<Any?>(value)
        cursor.addRow(values)
        return cursor
    }

    /**
     * 从uri中获取spname和key
     *
     * @param uri
     * @return
     */
    private fun getModel(uri: Uri): PreferencesProvider.Model? {
        try {
            val model: PreferencesProvider.Model = PreferencesProvider.Model()
            model.setSpName(uri.pathSegments[1])
            if (uri.pathSegments.size > 2) {
                model.setKey(uri.pathSegments[2])
            }
            if (uri.pathSegments.size > 3) {
                model.setDefValue(uri.pathSegments[3])
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
    private inner class Model {
        var spName: String? = null
        var key: String? = null
        var defValue: Any? = null
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