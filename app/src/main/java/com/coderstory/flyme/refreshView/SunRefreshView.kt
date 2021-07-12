package com.coderstory.flyme.refreshViewimport

import android.graphics.*
import android.graphics.drawable.Animatable
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.coderstory.flyme.R
import com.coderstory.flyme.tools.Utils
import com.coderstory.flyme.view.PullToRefreshView

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
import android.content.DialogInterface
import android.os.Bundle
import android.content.SharedPreferences
import android.content.ContextWrapper
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
import android.content.Intent
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
import android.content.ClipData
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
import android.content.ComponentName
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
import android.content.ContentProvider
import android.content.UriMatcher
import com.coderstory.flyme.preferences.PreferencesProvider
import android.content.ContentValues
import android.database.MatrixCursor
import android.content.ContentResolver
import android.view.animation.LinearInterpolator

/**
 * Created by Oleksii Shliama on 22/12/2014.
 * https://dribbble.com/shots/1650317-Pull-to-Refresh-Rentals
 */
class SunRefreshView(private val mParent: PullToRefreshView) : BaseRefreshView(mParent), Animatable {
    private val mMatrix: Matrix
    private val mSunSize = 100
    private var mAnimation: Animation? = null
    private var mTop = 0
    private var mScreenWidth = 0
    private var mSkyHeight = 0
    private var mSkyTopOffset = 0f
    private var mSkyMoveOffset = 0f
    private var mTownHeight = 0
    private var mTownInitialTopOffset = 0f
    private var mTownFinalTopOffset = 0f
    private var mTownMoveOffset = 0f
    private var mSunLeftOffset = 0f
    private var mSunTopOffset = 0f
    private var mPercent = 0.0f
    private var mRotate = 0.0f
    private var mSky: Bitmap? = null
    private var mSun: Bitmap? = null
    private var mTown: Bitmap? = null
    private var isRefreshing = false
    private fun initiateDimens(viewWidth: Int) {
        if (viewWidth <= 0 || viewWidth == mScreenWidth) return
        mScreenWidth = viewWidth
        mSkyHeight = (SunRefreshView.Companion.SKY_RATIO * mScreenWidth)
        mSkyTopOffset = mSkyHeight * 0.38f
        mSkyMoveOffset = Utils.Companion.convertDpToPixel(context, 15).toFloat()
        mTownHeight = (SunRefreshView.Companion.TOWN_RATIO * mScreenWidth)
        mTownInitialTopOffset = mParent.totalDragDistance - mTownHeight * SunRefreshView.Companion.TOWN_INITIAL_SCALE
        mTownFinalTopOffset = mParent.totalDragDistance - mTownHeight * SunRefreshView.Companion.TOWN_FINAL_SCALE
        mTownMoveOffset = Utils.Companion.convertDpToPixel(context, 10).toFloat()
        mSunLeftOffset = 0.3f * mScreenWidth.toFloat()
        mSunTopOffset = mParent.totalDragDistance * 0.1f
        mTop = -mParent.totalDragDistance
        createBitmaps()
    }

    private fun createBitmaps() {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        mSky = BitmapFactory.decodeResource(context.resources, R.drawable.sky, options)
        mSky = Bitmap.createScaledBitmap(mSky, mScreenWidth, mSkyHeight, true)
        mTown = BitmapFactory.decodeResource(context.resources, R.drawable.buildings, options)
        mTown = Bitmap.createScaledBitmap(mTown, mScreenWidth, (mScreenWidth * SunRefreshView.Companion.TOWN_RATIO) as Int, true)
        mSun = BitmapFactory.decodeResource(context.resources, R.drawable.sun, options)
        mSun = Bitmap.createScaledBitmap(mSun, mSunSize, mSunSize, true)
    }

    override fun setPercent(percent: Float, invalidate: Boolean) {
        setPercent(percent)
        if (invalidate) setRotate(percent)
    }

    override fun offsetTopAndBottom(offset: Int) {
        mTop += offset
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (mScreenWidth <= 0) return
        val saveCount = canvas.save()
        canvas.translate(0f, mTop.toFloat())
        canvas.clipRect(0, -mTop, mScreenWidth, mParent.totalDragDistance)
        drawSky(canvas)
        drawSun(canvas)
        drawTown(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun drawSky(canvas: Canvas) {
        val matrix = mMatrix
        matrix.reset()
        val dragPercent = Math.min(1f, Math.abs(mPercent))
        val skyScale: Float
        val scalePercentDelta: Float = dragPercent - SunRefreshView.Companion.SCALE_START_PERCENT
        skyScale = if (scalePercentDelta > 0) {
            val scalePercent: Float = scalePercentDelta / (1.0f - SunRefreshView.Companion.SCALE_START_PERCENT)
            SunRefreshView.Companion.SKY_INITIAL_SCALE - (SunRefreshView.Companion.SKY_INITIAL_SCALE - 1.0f) * scalePercent
        } else {
            SunRefreshView.Companion.SKY_INITIAL_SCALE
        }
        val offsetX = -(mScreenWidth * skyScale - mScreenWidth) / 2.0f
        val offsetY = (((1.0f - dragPercent) * mParent.totalDragDistance - mSkyTopOffset // Offset canvas moving
                - mSkyHeight * (skyScale - 1.0f) / 2) // Offset sky scaling
                + mSkyMoveOffset * dragPercent) // Give it a little move top -> bottom
        matrix.postScale(skyScale, skyScale)
        matrix.postTranslate(offsetX, offsetY)
        canvas.drawBitmap(mSky!!, matrix, null)
    }

    private fun drawTown(canvas: Canvas) {
        val matrix = mMatrix
        matrix.reset()
        val dragPercent = Math.min(1f, Math.abs(mPercent))
        val townScale: Float
        val townTopOffset: Float
        val townMoveOffset: Float
        val scalePercentDelta: Float = dragPercent - SunRefreshView.Companion.SCALE_START_PERCENT
        if (scalePercentDelta > 0) {
            val scalePercent: Float = scalePercentDelta / (1.0f - SunRefreshView.Companion.SCALE_START_PERCENT)
            townScale = SunRefreshView.Companion.TOWN_INITIAL_SCALE + (SunRefreshView.Companion.TOWN_FINAL_SCALE - SunRefreshView.Companion.TOWN_INITIAL_SCALE) * scalePercent
            townTopOffset = mTownInitialTopOffset - (mTownFinalTopOffset - mTownInitialTopOffset) * scalePercent
            townMoveOffset = mTownMoveOffset * (1.0f - scalePercent)
        } else {
            val scalePercent: Float = dragPercent / SunRefreshView.Companion.SCALE_START_PERCENT
            townScale = SunRefreshView.Companion.TOWN_INITIAL_SCALE
            townTopOffset = mTownInitialTopOffset
            townMoveOffset = mTownMoveOffset * scalePercent
        }
        val offsetX = -(mScreenWidth * townScale - mScreenWidth) / 2.0f
        val offsetY = ((1.0f - dragPercent) * mParent.totalDragDistance // Offset canvas moving
                + townTopOffset
                - mTownHeight * (townScale - 1.0f) / 2 // Offset town scaling
                + townMoveOffset) // Give it a little move
        matrix.postScale(townScale, townScale)
        matrix.postTranslate(offsetX, offsetY)
        canvas.drawBitmap(mTown!!, matrix, null)
    }

    private fun drawSun(canvas: Canvas) {
        val matrix = mMatrix
        matrix.reset()
        var dragPercent = mPercent
        if (dragPercent > 1.0f) { // Slow down if pulling over set height
            dragPercent = (dragPercent + 9.0f) / 10
        }
        val sunRadius = mSunSize.toFloat() / 2.0f
        var sunRotateGrowth: Float = SunRefreshView.Companion.SUN_INITIAL_ROTATE_GROWTH
        var offsetX = mSunLeftOffset
        var offsetY = (mSunTopOffset
                + mParent.totalDragDistance / 2 * (1.0f - dragPercent) // Move the sun up
                - mTop) // Depending on Canvas position
        val scalePercentDelta: Float = dragPercent - SunRefreshView.Companion.SCALE_START_PERCENT
        if (scalePercentDelta > 0) {
            val scalePercent: Float = scalePercentDelta / (1.0f - SunRefreshView.Companion.SCALE_START_PERCENT)
            val sunScale: Float = 1.0f - (1.0f - SunRefreshView.Companion.SUN_FINAL_SCALE) * scalePercent
            sunRotateGrowth += (SunRefreshView.Companion.SUN_FINAL_ROTATE_GROWTH - SunRefreshView.Companion.SUN_INITIAL_ROTATE_GROWTH) * scalePercent
            matrix.preTranslate(offsetX + (sunRadius - sunRadius * sunScale), offsetY * (2.0f - sunScale))
            matrix.preScale(sunScale, sunScale)
            offsetX += sunRadius
            offsetY = offsetY * (2.0f - sunScale) + sunRadius * sunScale
        } else {
            matrix.postTranslate(offsetX, offsetY)
            offsetX += sunRadius
            offsetY += sunRadius
        }
        matrix.postRotate(
                (if (isRefreshing) -360 else 360) * mRotate * if (isRefreshing) 1 else sunRotateGrowth,
                offsetX,
                offsetY)
        canvas.drawBitmap(mSun!!, matrix, null)
    }

    private fun setPercent(percent: Float) {
        mPercent = percent
    }

    private fun setRotate(rotate: Float) {
        mRotate = rotate
        invalidateSelf()
    }

    private fun resetOriginals() {
        setPercent(0f)
        setRotate(0f)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, mSkyHeight + top)
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun start() {
        mAnimation!!.reset()
        isRefreshing = true
        mParent.startAnimation(mAnimation)
    }

    override fun stop() {
        mParent.clearAnimation()
        isRefreshing = false
        resetOriginals()
    }

    private fun setupAnimations() {
        mAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setRotate(interpolatedTime)
            }
        }
        mAnimation.setRepeatCount(Animation.INFINITE)
        mAnimation.setRepeatMode(Animation.RESTART)
        mAnimation.setInterpolator(SunRefreshView.Companion.LINEAR_INTERPOLATOR)
        mAnimation.setDuration(SunRefreshView.Companion.ANIMATION_DURATION.toLong())
    }

    companion object {
        private const val SCALE_START_PERCENT = 0.5f
        private const val ANIMATION_DURATION = 1000
        private const val SKY_RATIO = 0.65f
        private const val SKY_INITIAL_SCALE = 1.05f
        private const val TOWN_RATIO = 0.22f
        private const val TOWN_INITIAL_SCALE = 1.20f
        private const val TOWN_FINAL_SCALE = 1.30f
        private const val SUN_FINAL_SCALE = 0.75f
        private const val SUN_INITIAL_ROTATE_GROWTH = 1.2f
        private const val SUN_FINAL_ROTATE_GROWTH = 1.5f
        private val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
    }

    init {
        mMatrix = Matrix()
        setupAnimations()
        mParent.post { initiateDimens(mParent.width) }
    }
}