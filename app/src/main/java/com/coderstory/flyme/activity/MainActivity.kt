package com.coderstory.flyme.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.*
import android.util.Log
import android.view.*
import android.widget.Toast
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
import com.coderstory.flyme.update.UpdgradeService
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.Layer
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks

class MainActivity : BaseActivity(), PermissionCallbacks {
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
                    normalDialog.setPositiveButton("确定"
                    ) { dialog: DialogInterface?, which: Int -> System.exit(0) }
                    normalDialog.show()
                    super.handleMessage(msg)
                }
                1 -> {
                    dialog = ProgressDialog.show(this@MainActivity, "检测ROOT权限", "请在ROOT授权弹窗中给与ROOT权限,\n如果长时间无反应则请检查自带的ROOT是否失效或者magisk是否允许后台运行")
                    dialog?.show()
                }
                2 -> if (dialog != null && dialog!!.isShowing) {
                    dialog!!.cancel()
                    helper.put("isRooted", true)
                }
                4 -> if (msg.data["value"] != "{\"error\":\"0\"}") {
                    Toast.makeText(this@MainActivity, Utils.Companion.decode("5Lya5ZGY5qCh6aqM5aSx6LSl") + ":\r\n" +
                            Gson().fromJson<Map<String, String>>(msg.data["value"].toString(), MutableMap::class.java).getOrDefault("error", msg.data["value"].toString()), Toast.LENGTH_LONG).show()
                    helper.put(Utils.decode("bWFyaw=="), "")
                }
                5 ->                     // 接口调用失败
                    Toast.makeText(this@MainActivity, "服务器连接失败", Toast.LENGTH_LONG).show()
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

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun setUpView() {
        requestCameraPermission()
        mToolbar = `$`(id.toolbar)
        mDrawerLayout = `$`(id.drawer_layout)
        mNavigationView = `$`(id.navigation_view)
        mToolbar!!.title = getString(R.string.othersettings)

        //这句一定要在下面几句之前调用，不然就会出现点击无反应
        setSupportActionBar(mToolbar)
        setNavigationViewItemClickListener()
        val mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close)
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
        if (Utils.Companion.check(helper)) {
            Thread(Utils().Check(helper, myHandler, this)).start()
        }
        if (helper.getBoolean("enableUpdate", true)) {
            UpdgradeService(this).checkUpgrade()
        }
    }

    private fun checkDialog() {
        if (Build.VERSION.SDK_INT == 30) {
            try {
                getSharedPreferences("test", MODE_WORLD_READABLE)
            } catch (e: SecurityException) {
//                val normalDialog = AlertDialog.Builder(this@MainActivity)
//                normalDialog.setTitle("配置设置失败警告")
//                normalDialog.setMessage("请在LSPosed Manager或者EdXposed Manager中启用本插件后再打开本插件")
//                normalDialog.setPositiveButton("确定"
//                ) { dialog: DialogInterface?, which: Int -> System.exit(0) }
//                normalDialog.show()
            }
        }
        try {
            val classType = Class.forName("android.os.SystemProperties")
            val getMethod = classType.getDeclaredMethod("get", String::class.java)
            val value = getMethod.invoke(classType, *arrayOf<Any>("ro.build.flyme.version")) as String
            Log.e("xposed", "当前flyme版本$value")
            if ("9" != value) {
                val normalDialog = android.app.AlertDialog.Builder(this@MainActivity)
                normalDialog.setTitle("不兼容的操作系统")
                if (value == null || "" == value) {
                    normalDialog.setMessage("当前助手适配的是Flyme9系统,而当前系统不是FLyme")
                } else {
                    normalDialog.setMessage("当前助手适配的是Flyme9系统,而当前系统是flyme$value,请选择合适的版本")
                }
                normalDialog.setPositiveButton("退出"
                ) { dialog: DialogInterface?, which: Int -> System.exit(0) }
                normalDialog.setCancelable(false)
                normalDialog.show()
            }
        } catch (e: Exception) {
            Log.e("", e.message, e)
        }
        if (helper.getBoolean("firstOpenD", true) && Build.VERSION.SDK_INT <= 28) {
            val normalDialog = android.app.AlertDialog.Builder(this@MainActivity)
            normalDialog.setTitle("提示")
            normalDialog.setMessage("部分涉及系统UI的功能在低版本安卓系统[7.0-9.0]上不可以用")
            normalDialog.setPositiveButton("确定"
            ) { dialog: DialogInterface?, which: Int -> helper.put("firstOpenD", false) }
            normalDialog.setCancelable(true)
            normalDialog.show()
        }
        if (!Utils.Companion.vi()) {
            val normalDialog = android.app.AlertDialog.Builder(this@MainActivity)
            normalDialog.setTitle("过期提示")
            normalDialog.setMessage("当前flyme助手版本已过期，请加入交流群下载最新版本")
            normalDialog.setPositiveButton("确定"
            ) { dialog: DialogInterface?, which: Int -> }
            normalDialog.setCancelable(true)
            normalDialog.show()
        }
        if (Misc.isTestVersion) {
            val normalDialog = android.app.AlertDialog.Builder(this@MainActivity)
            normalDialog.setTitle("FBI Warning")
            normalDialog.setMessage("当前版本为测试版本,不适合长期使用")
            normalDialog.setPositiveButton("确定"
            ) { dialog: DialogInterface?, which: Int -> }
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
                    .onClick({ AnyLayer: Layer, v: View? -> AnyLayer.dismiss() }, id.fl_dialog_yes)
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

    val isEnable: Boolean
        get() = false

    private fun setNavigationViewItemClickListener() {
        mNavigationView!!.setNavigationItemSelectedListener { item: MenuItem ->
            if (null != mPreMenuItem) {
                mPreMenuItem!!.isChecked = false
            }
            if (Misc.isProcessing) {
                SnackBarUtils.Companion.makeShort(mDrawerLayout, getString(R.string.isWorkingTips)).danger()
                return@setNavigationItemSelectedListener false
            }
            val itemId = item.itemId
            if (itemId == id.navigation_item_settings) {
                mToolbar!!.setTitle(R.string.others_appsettings)
                switchFragment(SettingsFragment::class.java)
            } else if (itemId == id.navigation_item_Clean) {
                mToolbar!!.setTitle(R.string.appclean)
                switchFragment(CleanFragment::class.java)
            } else if (itemId == id.navigation_item_disableapps) {
                mToolbar!!.setTitle(R.string.disableapp)
                switchFragment(DisbaleAppFragment::class.java)
            } else if (itemId == id.navigation_item_about) {
                startActivityWithoutExtras(AboutActivity::class.java)
            } else if (itemId == id.navigation_item_hide_app) {
                mToolbar!!.setTitle(R.string.hide_app_icon)
                switchFragment(HideAppFragment::class.java)
            } else if (itemId == id.navigation_item_otherssettings) {
                mToolbar!!.setTitle(R.string.othersettings)
                switchFragment(OthersFragment::class.java)
            } else if (itemId == id.navigation_item_Blog) {
                mToolbar!!.setTitle(R.string.blog)
                switchFragment(BlogFragment::class.java)
            } else if (itemId == id.navigation_item_updateList) {
                mToolbar!!.setTitle(R.string.updateList)
                switchFragment(UpgradeFragment::class.java)
            } else if (itemId == id.navigation_item_system_ui_settings) {
                mToolbar!!.setTitle(R.string.systemui_settings)
                switchFragment(SystemUIFragment::class.java)
            } else if (itemId == id.navigation_item_hosts) {
                mToolbar!!.setTitle(R.string.hosts)
                switchFragment(HostsFragment::class.java)
            } else if (itemId == id.navigation_item_about_me) {
                mToolbar!!.title = Utils.Companion.decode("5Lya5ZGY5r+A5rS7")
                switchFragment(AccountFragment::class.java)
            } else if (itemId == id.navigation_item_xposed_install) {
                mToolbar!!.title = "xposed框架安装"
                switchFragment(XposedFragment::class.java)
            } else if (itemId == id.navigation_item_core_patch_settings) {
                mToolbar!!.title = "核心破解"
                switchFragment(CorePatchFragment::class.java)
            }
            item.isChecked = true
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            mPreMenuItem = item
            false
        }
    }

    //切换Fragment
    private fun switchFragment(clazz: Class<*>) {
        val to: Fragment? = ViewUtils.createFragment(clazz)
        if (to!!.isAdded) {
            mFragmentManager!!.beginTransaction().replace(mCurrentFragment!!.id, to).commit()
        } else {
            mFragmentManager!!.beginTransaction().replace(mCurrentFragment!!.id, to).commit()
        }
        mCurrentFragment = to
    }

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
            SnackBarUtils.Companion.makeShort(mDrawerLayout, "再按一次退出").info()
            lastBackKeyDownTick = currentTick
        } else {
            finish()
            System.exit(0)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setTitle("提示").setRationale("为了能正常使用应用,请授权读写存储权限！").setPositiveButton("去设置").setNegativeButton("取消").setRequestCode(1).build().show()
        } else {
            Toast.makeText(this, "你拒绝了本权限，将无法使用部分功能", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setTitle("提示").setRationale("为了能正常使用应用,请授权读写存储权限！").setPositiveButton("去设置").setNegativeButton("取消").setRequestCode(1).build().show()
        } else {
            Toast.makeText(this, "你拒绝了本权限，将无法使用部分功能", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val MAX_DOUBLE_BACK_DURATION: Long = 1500
        private const val READ_EXTERNAL_STORAGE_CODE = 1
    }
}