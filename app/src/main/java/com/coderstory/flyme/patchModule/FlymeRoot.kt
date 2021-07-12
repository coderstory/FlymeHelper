package com.coderstory.flyme.patchModuleimport

import android.content.Context
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

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

class FlymeRoot : XposedHelper(), IModule {
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (loadPackageParam.packageName == "com.meizu.mznfcpay" && prefs!!.getBoolean("HideRootWithPay", false)) {
            // 6.0.7
            XposedHelper.Companion.findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context::class.java, XC_MethodReplacement.returnConstant(false))
        }
        if (loadPackageParam.packageName == "com.meizu.flyme.update" && prefs!!.getBoolean("HideRootWithUpgrade", false)) {
            // DEVICE_STATE_SERVICE
            if (findClassWithoutLog("com.meizu.cloud.a.a.a", loadPackageParam.classLoader) != null) {
                XposedHelper.Companion.findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context::class.java, XC_MethodReplacement.returnConstant(false))
            } else {
                XposedHelper.Companion.findAndHookMethod("com.meizu.cloud.a.b.a", loadPackageParam.classLoader, "c", Context::class.java, XC_MethodReplacement.returnConstant(false))
            }
        }

        // hook 框架层的root检测
        if ("android" == loadPackageParam.packageName) {
            XposedHelper.Companion.hookAllMethods("com.android.server.DeviceStateService", loadPackageParam.classLoader, "doCheckState", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val code = param.args[0] as Int
                    if (prefs!!.getBoolean("HideRootGlobal", false)) {
                        if (code == 1 || code == 3 || code == 4) {
                            param.result = 0
                        }
                    }
                }
            })
        }
    }

    override fun initZygote(startupParam: StartupParam?) {}
    override fun handleInitPackageResources(initPackageResourcesParam: InitPackageResourcesParam) {}
}