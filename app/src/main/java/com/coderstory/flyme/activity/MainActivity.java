package com.coderstory.flyme.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSON;
import com.coderstory.flyme.R;
import com.coderstory.flyme.activity.base.BaseActivity;
import com.coderstory.flyme.fragment.AboutMeFragment;
import com.coderstory.flyme.fragment.BlogFragment;
import com.coderstory.flyme.fragment.CleanFragment;
import com.coderstory.flyme.fragment.DisbaleAppFragment;
import com.coderstory.flyme.fragment.HideAppFragment;
import com.coderstory.flyme.fragment.HostsFragment;
import com.coderstory.flyme.fragment.OthersFragment;
import com.coderstory.flyme.fragment.SettingsFragment;
import com.coderstory.flyme.fragment.SystemUIFragment;
import com.coderstory.flyme.fragment.UpdateListFragment;
import com.coderstory.flyme.fragment.WebViewFragment;
import com.coderstory.flyme.fragment.XposedFragment;
import com.coderstory.flyme.update.updgradeService;
import com.coderstory.flyme.utils.Misc;
import com.coderstory.flyme.utils.SharedHelper;
import com.coderstory.flyme.utils.SnackBarUtils;
import com.coderstory.flyme.utils.Utils;
import com.coderstory.flyme.utils.ViewUtils;
import com.google.android.material.navigation.NavigationView;
import com.topjohnwu.superuser.Shell;

import java.util.List;

import per.goweii.anylayer.AnyLayer;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.coderstory.flyme.R.id.navigation_view;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    private static final int READ_EXTERNAL_STORAGE_CODE = 1;
    private final SharedHelper helper = new SharedHelper(this);
    private DrawerLayout mDrawerLayout;//侧边菜单视图
    private Toolbar mToolbar;
    private NavigationView mNavigationView;//侧边菜单项
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private MenuItem mPreMenuItem;
    private long lastBackKeyDownTick = 0;
    private ProgressDialog dialog;
    @SuppressLint("HandlerLeak")
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 0:
                    final androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("请先授权应用ROOT权限(或者你的ROOT已失效)");
                    normalDialog.setPositiveButton("确定",
                            (dialog, which) -> System.exit(0));
                    normalDialog.show();
                    super.handleMessage(msg);
                    break;
                case 1:
                    dialog = ProgressDialog.show(MainActivity.this, "检测ROOT权限", "请在ROOT授权弹窗中给与ROOT权限,\n如果长时间无反应则请检查自带的ROOT是否失效或者magisk是否允许后台运行");
                    dialog.show();
                    break;
                case 2:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.cancel();
                        helper.put("isRooted", true);
                    }
                    break;
                case 4:
                    if (!msg.getData().get("value").equals("{\"error\":\"0\"}")) {
                        Toast.makeText(MainActivity.this, Utils.decode("5Lya5ZGY5qCh6aqM5aSx6LSl") + ":\r\n" + JSON.parseObject(msg.getData().get("value").toString()).getOrDefault("error", msg.getData().get("value").toString()), Toast.LENGTH_LONG).show();
                        helper.put(Utils.decode("bWFyaw=="), "");
                        //helper.put("sn", "");
                    }
                    // 校验返回
                    break;
                case 5:
                    // 接口调用失败
                    Toast.makeText(MainActivity.this, "服务器连接失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("android:support:fragments", null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        mFragmentManager = getSupportFragmentManager();
    }

    private void requestCameraPermission() {
        MainActivity.this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List perms) {//权限拒绝
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setTitle("提示").setRationale("为了能正常使用应用,请授权读写存储权限！").setPositiveButton("去设置").setNegativeButton("取消").setRequestCode(1).build().show();
        } else {
            Toast.makeText(this, "你拒绝了本权限，将无法使用部分功能", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void setUpView() {
        requestCameraPermission();
        mToolbar = $(R.id.toolbar);
        mDrawerLayout = $(R.id.drawer_layout);
        mNavigationView = $(navigation_view);

        mToolbar.setTitle(getString(R.string.othersettings));

        //这句一定要在下面几句之前调用，不然就会出现点击无反应
        setSupportActionBar(mToolbar);
        setNavigationViewItemClickListener();
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mToolbar.setNavigationIcon(R.drawable.ic_drawer_home);
        initDefaultFragment();

        initData();
    }

    private void initData() {
        //if (!helper.getBoolean("isRooted", false)) {
        // 检测弹窗
        new Thread(() -> {
            Message msg = new Message();
            msg.arg1 = 1;
            myHandler.sendMessage(msg);
            if (!Shell.su("").exec().isSuccess()) {
                msg = new Message();
                msg.arg1 = 0;
                myHandler.sendMessage(msg);
            } else {
                msg = new Message();
                msg.arg1 = 2;
                myHandler.sendMessage(msg);
                // copySo();
            }
        }).start();

        checkEnable();

        if (helper.getBoolean("firstOpenC", true)) {
        }

        if (helper.getBoolean("firstOpenD", true)) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
            normalDialog.setTitle("!!重要提示!!");
            normalDialog.setMessage("部分功能，比如时间居中只有升级到android 10后才能使用");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> {
                        helper.put("firstOpenD", false);
                    });
            normalDialog.setCancelable(true);
            normalDialog.show();
        }

        if (!Utils.vi()) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
            normalDialog.setTitle("过期提示");
            normalDialog.setMessage("当前flyme助手版本已过期，请加入交流群下载最新版本");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> {
                    });
            normalDialog.setCancelable(true);
            normalDialog.show();
        }

        if (Misc.isTestVersion) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
            normalDialog.setTitle("FBI Warning");
            normalDialog.setMessage("当前版本为测试版本,不适合长期使用");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> {
                    });
            normalDialog.setCancelable(true);
            normalDialog.show();
        }

        if (Utils.check(helper)) {
            new Thread(new Utils().new Check(helper, myHandler, this)).start();
        }

        new updgradeService(this).checkUpgrade();
    }

    private void checkEnable() {
        Log.e("xposed", "flyme助手->isEnable:" + (isEnable() ? "true" : "false"));
        if (helper.getBoolean("enableCheck", true) && !isEnable()) {
            AnyLayer.dialog(MainActivity.this)
                    .contentView(R.layout.dialog_xposed_disabled)
                    .cancelableOnTouchOutside(false)
                    .cancelableOnClickKeyBack(false)
                    .onClick((AnyLayer, v) -> AnyLayer.dismiss(), R.id.fl_dialog_yes)
                    .show();
        }
    }

    //init the default checked fragment
    private void initDefaultFragment() {
        mCurrentFragment = ViewUtils.createFragment(OthersFragment.class);
        mFragmentManager.beginTransaction().add(R.id.frame_content, mCurrentFragment).commit();
        mPreMenuItem = mNavigationView.getMenu().getItem(0);
        mPreMenuItem.setChecked(true);
    }

    public boolean isEnable() {
        return false;
    }

    private void setNavigationViewItemClickListener() {

        mNavigationView.setNavigationItemSelectedListener(item -> {
            if (null != mPreMenuItem) {
                mPreMenuItem.setChecked(false);
            }
            if (Misc.isProcessing) {
                SnackBarUtils.makeShort(mDrawerLayout, getString(R.string.isWorkingTips)).danger();
                return false;
            }

            switch (item.getItemId()) {

                case R.id.navigation_item_settings:
                    mToolbar.setTitle(R.string.others_appsettings);
                    switchFragment(SettingsFragment.class);
                    break;

                case R.id.navigation_item_Clean:
                    mToolbar.setTitle(R.string.appclean);
                    switchFragment(CleanFragment.class);
                    break;

                case R.id.navigation_item_disableapps:
                    mToolbar.setTitle(R.string.disableapp);
                    switchFragment(DisbaleAppFragment.class);
                    break;
                case R.id.navigation_item_about:
                    startActivityWithoutExtras(AboutActivity.class);
                    break;
                case R.id.navigation_item_hide_app:
                    mToolbar.setTitle(R.string.hide_app_icon);
                    switchFragment(HideAppFragment.class);
                    break;

                case R.id.navigation_item_otherssettings:
                    mToolbar.setTitle(R.string.othersettings);
                    switchFragment(OthersFragment.class);
                    break;
                case R.id.navigation_item_Blog:
                    mToolbar.setTitle(R.string.blog);
                    switchFragment(BlogFragment.class);
                    break;
                case R.id.navigation_item_updateList:
                    mToolbar.setTitle(R.string.updateList);
                    switchFragment(UpdateListFragment.class);
                    break;
                case R.id.navigation_item_system_ui_settings:
                    mToolbar.setTitle(R.string.systemui_settings);
                    switchFragment(SystemUIFragment.class);
                    break;
                case R.id.navigation_item_hosts:
                    mToolbar.setTitle(R.string.hosts);
                    switchFragment(HostsFragment.class);
                    break;
                case R.id.navigation_item_about_me:
                    mToolbar.setTitle(Utils.decode("5Lya5ZGY5r+A5rS7"));
                    switchFragment(AboutMeFragment.class);
                    break;
                case R.id.navigation_item_xposed_install:
                    mToolbar.setTitle("xposed框架安装");
                    switchFragment(XposedFragment.class);
                    break;
                default:
                    break;
            }
            item.setChecked(true);
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mPreMenuItem = item;
            return false;
        });
    }

    //切换Fragment
    private void switchFragment(Class<?> clazz) {
        Fragment to = ViewUtils.createFragment(clazz);
        if (to.isAdded()) {
            mFragmentManager.beginTransaction().replace(mCurrentFragment.getId(), to).commit();
        } else {
            mFragmentManager.beginTransaction().replace(mCurrentFragment.getId(), to).commit();
        }
        mCurrentFragment = to;
    }

    @Override
    public void onBackPressed() {
        //当前抽屉是打开的，则关闭
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        //如果当前的Fragment是WebViewFragment 则监听返回事件
        if (mCurrentFragment instanceof WebViewFragment) {
            WebViewFragment webViewFragment = (WebViewFragment) mCurrentFragment;
            if (webViewFragment.canGoBack()) {
                webViewFragment.goBack();
                return;
            }
        }

        long currentTick = System.currentTimeMillis();
        if (currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION) {
            SnackBarUtils.makeShort(mDrawerLayout, "再按一次退出").info();
            lastBackKeyDownTick = currentTick;
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }
}



