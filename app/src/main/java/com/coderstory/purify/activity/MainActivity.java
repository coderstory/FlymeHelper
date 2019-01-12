package com.coderstory.purify.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.coderstory.purify.R;
import com.coderstory.purify.activity.base.BaseActivity;
import com.coderstory.purify.config.Misc;
import com.coderstory.purify.fragment.CleanFragment;
import com.coderstory.purify.fragment.DisbaleAppFragment;
import com.coderstory.purify.fragment.HideAppFragment;
import com.coderstory.purify.fragment.HostsFragment;
import com.coderstory.purify.fragment.ManagerAppFragment;
import com.coderstory.purify.fragment.OthersFragment;
import com.coderstory.purify.fragment.SettingsFragment;
import com.coderstory.purify.fragment.UpdateListFragment;
import com.coderstory.purify.fragment.WebViewFragment;
import com.coderstory.purify.utils.SnackBarUtils;
import com.coderstory.purify.utils.ViewUtils;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import per.goweii.anylayer.AnyLayer;

import static com.coderstory.purify.R.id.navigation_view;

public class MainActivity extends BaseActivity {
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    private static final int READ_EXTERNAL_STORAGE_CODE = 1;
    private DrawerLayout mDrawerLayout;//侧边菜单视图
    private Toolbar mToolbar;
    private NavigationView mNavigationView;//侧边菜单项
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private MenuItem mPreMenuItem;
    private long lastBackKeyDownTick = 0;
    private ProgressDialog dialog;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MainActivity.this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Log.i("MainActivity", "onRequestPermissionsResult granted=" + granted);
        }
    }

    @Override
    protected void setUpView() {
        mToolbar = $(R.id.toolbar);
        mDrawerLayout = $(R.id.drawer_layout);
        mNavigationView = $(navigation_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(MainActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                requestCameraPermission();
            }
        }

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

//        if(getInstance().getBoolean("firstOpen",true)){
//            getInstance().saveConfig("firstOpen",false);
//            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
//            normalDialog.setTitle("提示");
//            normalDialog.setMessage("本次更新后Xposed功能不再依赖ROOT权限,所有设置恢复默认，请重新设置。");
//            normalDialog.setPositiveButton("确定",
//                    (dialog, which) -> {});
//            normalDialog.setCancelable(true);
//            normalDialog.show();
//        }
    }

    private void checkEnable() {
        Log.e("xposed", "flyme助手->isEnable:" + (isEnable() ? "true" : "false"));
        if (MainActivity.this.getSharedPreferences(Misc.SharedPreferencesName, Context.MODE_PRIVATE).getBoolean("enableCheck", true) && !isEnable()) {

            AnyLayer.with(MainActivity.this)
                    .contentView(R.layout.dialog_xposed_disabled)
                    .backgroundColorRes(R.color.dialog_bg)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick(R.id.fl_dialog_yes, (AnyLayer, v) -> AnyLayer.dismiss())
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

                case R.id.navigation_item_hosts:
                    mToolbar.setTitle(R.string.hosts);
                    switchFragment(HostsFragment.class);
                    break;

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
                case R.id.navigation_item_ManagerApp:
                    mToolbar.setTitle(R.string.navigation_item_ManagerApp);
                    switchFragment(ManagerAppFragment.class);
                    break;
                case R.id.navigation_item_hide_app:
                    mToolbar.setTitle(R.string.hide_app_icon);
                    switchFragment(HideAppFragment.class);
                    break;

                case R.id.navigation_item_otherssettings:
                    mToolbar.setTitle(R.string.othersettings);
                    switchFragment(OthersFragment.class);
                    break;
                case R.id.navigation_item_updateList:
                    mToolbar.setTitle(R.string.updateList);
                    switchFragment(UpdateListFragment.class);
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
}
