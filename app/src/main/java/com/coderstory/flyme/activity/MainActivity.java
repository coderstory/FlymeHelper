package com.coderstory.flyme.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import com.coderstory.flyme.R;
import com.coderstory.flyme.activity.base.BaseActivity;
import com.coderstory.flyme.config.Misc;
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
import com.coderstory.flyme.utils.SharedHelper;
import com.coderstory.flyme.utils.SnackBarUtils;
import com.coderstory.flyme.utils.ViewUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.coderstory.flyme.R.id.navigation_view;
import static com.coderstory.flyme.utils.Utils.vi;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    private static final int READ_EXTERNAL_STORAGE_CODE = 1;
    private DrawerLayout mDrawerLayout;//侧边菜单视图
    private Toolbar mToolbar;
    private NavigationView mNavigationView;//侧边菜单项
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private MenuItem mPreMenuItem;
    private long lastBackKeyDownTick = 0;
    private SharedHelper helper = new SharedHelper(this);

    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(MainActivity.this);
            normalDialog.setTitle("提示");
            normalDialog.setMessage("请先授权应用ROOT权限");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> System.exit(0));
            // 显示
            normalDialog.show();
            super.handleMessage(msg);
        }
    };

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

        checkEnable();

        new Thread(() -> {
            if (!Shell.SU.available()) {
                myHandler.sendMessage(new Message());
            }
        }).start();

        if (helper.getBoolean("firstOpenB", true)) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
            normalDialog.setTitle("初始提示");
            normalDialog.setMessage("flyme助手是基于xposed框架开发的插件，使用本插件前请确保已经安装并激活了xposed/edxposed框架");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> {
                        helper.put("firstOpenB", false);
                    });
            normalDialog.setCancelable(true);
            normalDialog.show();
            copySo();
        }

        if (!vi()) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
            normalDialog.setTitle("过期提示");
            normalDialog.setMessage("当前flyme助手版本已过期，请下载最新版本");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> {
                    });
            normalDialog.setCancelable(true);
            normalDialog.show();
        }

        if (Misc.isTestVersion) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
            normalDialog.setTitle("FBI Warning");
            normalDialog.setMessage("当前版本为测试版本,不适合长期使用,且存在3天的有效期,过期后功能会无法使用");
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> {
                    });
            normalDialog.setCancelable(true);
            normalDialog.show();
        }
    }

    private void checkEnable() {
        Log.e("xposed", "flyme助手->isEnable:" + (isEnable() ? "true" : "false"));

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
                    mToolbar.setTitle(R.string.systemui);
                    switchFragment(SystemUIFragment.class);
                    break;
                case R.id.navigation_item_hosts:
                    mToolbar.setTitle(R.string.hosts);
                    switchFragment(HostsFragment.class);
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


    public void copySo() {
        ///data/app/com.coderstory.flyme-BXZlEdHOp7SsF02Yd3u8BA==/base.apk
        String path = getPackageResourcePath().replace("/base.apk", "") + "/lib/arm64/libnc.so";
        Shell.SU.run("echo " + path + " > /data/config.cfg");
        Shell.SU.run("chmod 0777 " + path + " /data/config.cfg");
    }
}
