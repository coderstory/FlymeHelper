package com.coderstory.purify.module;

import android.content.Context;
import android.widget.Toast;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.purify.utils.ConfigPreferences.getInstance;


public class Others extends XposedHelper implements IModule {

    private static Context mContext = null;

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        // 禁止安装app时候的安全检验
        if (loadPackageParam.packageName.equals("com.android.packageinstaller")) {

            if (getInstance().getBoolean("enableCheckInstaller", false)) {
                // 8.x
                Class clazz = findClass("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader);
                if (clazz == null) {
                    // 7.x
                    clazz = findClass("com.android.packageinstaller.PackageInstallerActivity", loadPackageParam.classLoader);
                }
                if (clazz != null) {
                    findAndHookMethod(clazz, "setVirusCheckTime", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            Object mHandler = XposedHelpers.getObjectField(param.thisObject, "mHandler");
                            XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 5);
                            return null;
                        }
                    });
                }
            }
            if (getInstance().getBoolean("enableCTS", false)) {
                XposedBridge.log("开启原生安装器");
                findAndHookMethod("com.meizu.safe.security.utils.Utils", loadPackageParam.classLoader, "isCtsRunning", XC_MethodReplacement.returnConstant(true));
            }
        }

        if (loadPackageParam.packageName.equals("com.meizu.flyme.update")) {

            // 获取Context
            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.d.a", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = (Context) param.args[0];
                }
            });

            // 解析当前系统版本 待更新版本的zip包地址
            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.model.k", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object obj = param.thisObject;

                    Object currentFimware = XposedHelpers.getObjectField(obj, "currentFimware");
                    handleInfo(currentFimware);

                    Object upgradeFirmware = XposedHelpers.getObjectField(obj, "upgradeFirmware");
                    handleInfo(upgradeFirmware);
                }
            });
        }

    }

    boolean handleInfo(Object info) {
        boolean needToast = false;
        if (info != null) {
            String update = getInstance().getString("updateList", "");
            String systemVersion = (String) XposedHelpers.getObjectField(info, "systemVersion");
            String updateUrl = (String) XposedHelpers.getObjectField(info, "updateUrl");
            String releaseDate = (String) XposedHelpers.getObjectField(info, "releaseDate");
            String fileSize = (String) XposedHelpers.getObjectField(info, "fileSize");
            if (!update.contains(releaseDate)) {
                needToast = true;
                update += systemVersion + "@" + updateUrl + "@" + fileSize + "@" + releaseDate + ";";
                getInstance().saveConfig("updateList", update);
                if (mContext != null) {
                    Toast.makeText(mContext, "flyme助手:已检测到新的更新包地址", Toast.LENGTH_LONG).show();
                }
            }
        }
        return needToast;
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

}
