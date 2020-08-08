package com.coderstory.flyme.plugins;

import android.util.Log;

import com.coderstory.flyme.BuildConfig;
import com.coderstory.flyme.module.FlymeHome;
import com.coderstory.flyme.module.FlymeRoot;
import com.coderstory.flyme.module.HideApp;
import com.coderstory.flyme.module.IsEnable;
import com.coderstory.flyme.module.Others;
import com.coderstory.flyme.module.RemoveAds;
import com.coderstory.flyme.module.SystemUi;
import com.coderstory.flyme.module.ThemePatcher;
import com.coderstory.flyme.utils.FileUtils;
import com.coderstory.flyme.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class start extends XposedHelper implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    {
        try {
            String path = FileUtils.readFile("/data/config.cfg");
            System.load(path);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            Log.println(Log.ERROR, "xx", "so加载失败222" + e.getMessage());
        }
    }


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
       // if (vi()) {
            new FlymeRoot().handleInitPackageResources(resparam);
            new FlymeHome().handleInitPackageResources(resparam);
            new Others().handleInitPackageResources(resparam);
            new SystemUi().handleInitPackageResources(resparam);
        // }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        //if (vi()) {
        new FlymeHome().handleLoadPackage(lpparam);
        new IsEnable().handleLoadPackage(lpparam);
        new HideApp().handleLoadPackage(lpparam);
        new Others().handleLoadPackage(lpparam);
        new ThemePatcher().handleLoadPackage(lpparam);
        new FlymeRoot().handleLoadPackage(lpparam);
        new RemoveAds().handleLoadPackage(lpparam);
        new SystemUi().handleLoadPackage(lpparam);
        // }
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        XposedBridge.log("Flyme8助手 " + BuildConfig.VERSION_NAME + " 开始Patch");
        //XposedBridge.log(" 产品有效期:" + Misc.endTime);
        // XposedBridge.log("激活状态:" + vi());
        //XposedBridge.log("SDK版本号: " + android.os.Build.VERSION.SDK_INT);
        //XposedBridge.log("exists" + new File("/data/user_de/0/" + ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml").exists());
    }
}
