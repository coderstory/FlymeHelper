package com.coderstory.flyme.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.*
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.coderstory.flyme.R
import com.coderstory.flyme.R.id
import com.coderstory.flyme.activity.base.BaseActivity
import com.coderstory.flyme.fragment.*
import com.coderstory.flyme.tools.*
import com.google.android.material.navigation.NavigationView
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.Layer
import kotlin.system.exitProcess

class MainActivity : BaseActivity() {
    private val isEnable: Boolean
        get() = false
    private val helper = SharedHelper(this)
    private var mDrawerLayout //侧边菜单视图
            : DrawerLayout? = null
    private var mToolbar: Toolbar? = null
    private var mNavigationView //侧边菜单项
            : NavigationView? = null
    private var mFragmentManager: FragmentManager? = null
    private var mCurrentFragment: Fragment? = null
    private var mPreMenuItem: MenuItem? = null
    private var lastBackKeyDownTick: Long = 0
    private var dialog: ProgressDialog? = null

    @SuppressLint("HandlerLeak")
    var myHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.arg1) {
                0 -> {
                    val normalDialog = AlertDialog.Builder(this@MainActivity)
                    normalDialog.setTitle("提示")
                    normalDialog.setMessage("请先授权应用ROOT权限")
                    normalDialog.setPositiveButton(
                        "确定"
                    ) { _: DialogInterface?, _: Int -> exitProcess(0) }
                    normalDialog.show()
                    super.handleMessage(msg)
                }

                1 -> {
                    dialog = ProgressDialog.show(
                        this@MainActivity,
                        "检测ROOT权限",
                        "请在ROOT授权弹窗中给与ROOT权限,\n如果长时间无反应则请检查自带的ROOT是否失效或者magisk是否允许后台运行"
                    )
                    dialog?.show()
                }

                2 -> if (dialog != null && dialog!!.isShowing) {
                    dialog!!.cancel()
                    helper.put("isRooted", true)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("android:support:fragments", null)
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        mFragmentManager = supportFragmentManager
    }

    override fun setUpView() {
        mToolbar = `$`(id.toolbar)
        mDrawerLayout = `$`(id.drawer_layout)
        mNavigationView = `$`(id.navigation_view)
        mToolbar!!.title = getString(R.string.othersettings)

        //这句一定要在下面几句之前调用，不然就会出现点击无反应
        setSupportActionBar(mToolbar)
        setNavigationViewItemClickListener()
        val mDrawerToggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            mToolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        mDrawerToggle.syncState()
        mDrawerLayout!!.addDrawerListener(mDrawerToggle)
        mToolbar!!.setNavigationIcon(R.drawable.ic_drawer_home)
        initDefaultFragment()
        initData()
    }

    private fun initData() {
        // 检测弹窗
        Thread {
            var msg = Message()
            msg.arg1 = 1
            myHandler.sendMessage(msg)
            if (!RuntimeUtil.hasRooted()) {
                msg = Message()
                msg.arg1 = 0
                myHandler.sendMessage(msg)
            } else {
                msg = Message()
                msg.arg1 = 2
                myHandler.sendMessage(msg)
            }
        }.start()
        checkEnable()
        checkDialog()
    }

    private fun checkDialog() {
        try {
            getSharedPreferences("test", MODE_WORLD_READABLE)
        } catch (e: SecurityException) {
            val normalDialog = AlertDialog.Builder(this@MainActivity)
            normalDialog.setCancelable(true)
            normalDialog.setTitle("插件配置初始化失败")
            normalDialog.setMessage("请在Lsposed中启用本插件，然后手动重启本软件再试")
            normalDialog.setPositiveButton(
                "确定"
            ) { _: DialogInterface?, _: Int -> Process.killProcess(Process.myPid()) }
            normalDialog.show()
        }

        try {
            val classType = Class.forName("android.os.SystemProperties")
            val getMethod = classType.getDeclaredMethod("get", String::class.java)
            val value = getMethod.invoke(classType, "ro.build.flyme.version") as String
            Log.e("xposed", "当前flyme版本$value")

        } catch (e: Exception) {
            Log.e("flyme10helper", e.message, e)
        }
        if (helper.getBoolean("firstOpenD", true) && Build.VERSION.SDK_INT <= 28) {
            val normalDialog = android.app.AlertDialog.Builder(this@MainActivity)
            normalDialog.setTitle("提示")
            normalDialog.setMessage("部分涉及系统UI的功能在低版本安卓系统[7.0-9.0]上不可以用")
            normalDialog.setPositiveButton(
                "确定"
            ) { _: DialogInterface?, _: Int -> helper.put("firstOpenD", false) }
            normalDialog.setCancelable(true)
            normalDialog.show()
        }

        if (Misc.isTestVersion) {
            val normalDialog = android.app.AlertDialog.Builder(this@MainActivity)
            normalDialog.setTitle("FBI Warning")
            normalDialog.setMessage("当前版本为测试版本,不适合长期使用")
            normalDialog.setPositiveButton(
                "确定"
            ) { _: DialogInterface?, _: Int -> }
            normalDialog.setCancelable(true)
            normalDialog.show()
        }
    }

    private fun checkEnable() {
        Log.e("xposed", "flyme助手->isEnable:" + if (isEnable) "true" else "false")
        if (helper.getBoolean("enableCheck", true) && !isEnable) {
            AnyLayer.dialog(this@MainActivity)
                .contentView(R.layout.dialog_xposed_disabled)
                .cancelableOnTouchOutside(false)
                .cancelableOnClickKeyBack(false)
                .onClick({ AnyLayer: Layer, _: View? -> AnyLayer.dismiss() }, id.fl_dialog_yes)
                .show()
        }
    }

    //init the default checked fragment
    private fun initDefaultFragment() {
        mCurrentFragment = ViewUtils.createFragment(OthersFragment::class.java)
        mFragmentManager!!.beginTransaction().add(id.frame_content, mCurrentFragment!!).commit()
        mPreMenuItem = mNavigationView!!.menu.getItem(0)
        mPreMenuItem?.isChecked = true
    }


    private fun setNavigationViewItemClickListener() {
        mNavigationView!!.setNavigationItemSelectedListener { item: MenuItem ->
            if (null != mPreMenuItem) {
                mPreMenuItem!!.isChecked = false
            }
            if (Misc.isProcessing) {
                SnackBarUtils.makeShort(mDrawerLayout, getString(R.string.isWorkingTips)).danger()
                return@setNavigationItemSelectedListener false
            }
            when (item.itemId) {
                id.navigation_item_settings -> {
                    mToolbar!!.setTitle(R.string.others_appsettings)
                    switchFragment(SettingsFragment::class.java)
                }

                id.navigation_item_disableapps -> {
                    mToolbar!!.setTitle(R.string.disableapp)
                    switchFragment(DisbaleAppFragment::class.java)
                }

                id.navigation_item_about -> {
                    startActivityWithoutExtras(AboutActivity::class.java)
                }

                id.navigation_item_otherssettings -> {
                    mToolbar!!.setTitle(R.string.othersettings)
                    switchFragment(OthersFragment::class.java)
                }

                id.navigation_item_Blog -> {
                    mToolbar!!.setTitle(R.string.blog)
                    switchFragment(BlogFragment::class.java)
                }

                id.navigation_item_updateList -> {
                    mToolbar!!.setTitle(R.string.updateList)
                    switchFragment(UpgradeFragment::class.java)
                }

                id.navigation_item_system_ui_settings -> {
                    mToolbar!!.setTitle(R.string.systemui_settings)
                    switchFragment(SystemUIFragment::class.java)
                }
            }
            item.isChecked = true
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            mPreMenuItem = item
            false
        }
    }

    //切换Fragment
    private fun switchFragment(clazz: Class<*>) {
        val to: Fragment = ViewUtils.createFragment(clazz)
        if (to.isAdded) {
            mFragmentManager!!.beginTransaction().replace(mCurrentFragment!!.id, to).commit()
        } else {
            mFragmentManager!!.beginTransaction().replace(mCurrentFragment!!.id, to).commit()
        }
        mCurrentFragment = to
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //当前抽屉是打开的，则关闭
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            return
        }
        //如果当前的Fragment是WebViewFragment 则监听返回事件
        if (mCurrentFragment is WebViewFragment) {
            val webViewFragment = mCurrentFragment as WebViewFragment
            if (webViewFragment.canGoBack()) {
                webViewFragment.goBack()
                return
            }
        }
        val currentTick = System.currentTimeMillis()
        if (currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION) {
            SnackBarUtils.makeShort(mDrawerLayout, "再按一次退出").info()
            lastBackKeyDownTick = currentTick
        } else {
            finish()
            exitProcess(0)
        }
    }

    companion object {
        const val MAX_DOUBLE_BACK_DURATION: Long = 1500
    }
}