package com.coderstory.flyme.module;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.SharedHelper;
import com.coderstory.flyme.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Others extends XposedHelper implements IModule {

    private static Context mContext = null;

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        // 禁止安装app时候的安全检验
        if (loadPackageParam.packageName.equals("com.android.packageinstaller")) {

            if (prefs.getBoolean("enableCheckInstaller", false)) {
                // 8.x
                Class clazz = findClass("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader);
                if (clazz != null) {
                    findAndHookMethod(clazz, "setVirusCheckTime", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            Object mHandler = XposedHelpers.getObjectField(param.thisObject, "mHandler");
                            XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 5);
                            return null;
                        }
                    });
                    findAndHookMethod("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader, "replaceOrInstall", String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedHelpers.setObjectField(param.thisObject, "mAppInfo", null);
                        }
                    });
                }
            }
            if (prefs.getBoolean("enableCTS", false)) {
                //XposedBridge.log("开启原生安装器");
                findAndHookMethod("com.meizu.safe.security.utils.Utils", loadPackageParam.classLoader, "isCtsRunning", XC_MethodReplacement.returnConstant(true));
            }
        }

        if (loadPackageParam.packageName.equals("com.meizu.flyme.update")) {

            // 获取Context
            // public abstract class a<T> implements ErrorListener, Listener {
            //     public a(Context context) {
            //        this.b = context.getApplicationContext();
            //        this.c = RequestManager.getInstance(this.b);
            //    }
            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.c.a", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.args[0] instanceof Context) {
                        mContext = (Context) param.args[0];
                    }
                }
            });
            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.d.a", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.args[0] instanceof Context) {
                        mContext = (Context) param.args[0];
                    }
                }
            });

            // 解析当前系统版本 待更新版本的zip包地址
            //       public class k {
            //                public b cdnCheckResult;
            //                public e currentFimware;
            //                public g firmwarePlan;
            //                public UpgradeFirmware upgradeFirmware;
            //
            //                public k(UpgradeFirmware upgradeFirmware, e eVar, g gVar, b bVar) {
            //                    this.upgradeFirmware = upgradeFirmware;
            //                    this.currentFimware = eVar;
            //                    this.firmwarePlan = gVar;
            //                    this.cdnCheckResult = bVar;
            //                }
            //            }

            XposedBridge.hookAllConstructors(findClass("com.meizu.flyme.update.model.n", loadPackageParam.classLoader), new XC_MethodHook() {
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


    private boolean handleInfo(Object info) {
        boolean needToast = false;
        if (info != null) {
            String update = new SharedHelper(mContext).getString("updateList", "");
            // update = new String(android.util.Base64.decode(update, Base64.DEFAULT));
            String systemVersion = (String) XposedHelpers.getObjectField(info, "systemVersion");
            String updateUrl = (String) XposedHelpers.getObjectField(info, "updateUrl");
            String releaseDate = (String) XposedHelpers.getObjectField(info, "releaseDate");
            String fileSize = (String) XposedHelpers.getObjectField(info, "fileSize");
            String msg = systemVersion + "@" + updateUrl + "@" + fileSize + "@" + releaseDate;
            if (!update.contains(msg)) {
                needToast = true;
                update += msg + ";";
                if (mContext != null) {
                    XposedBridge.log("参数保存结果" + new SharedHelper(mContext).put("updateList", android.util.Base64.encodeToString(update.getBytes(), Base64.DEFAULT)));
                    Toast.makeText(mContext, "flyme助手:已检测到新的更新包地址", Toast.LENGTH_LONG).show();
                    //XposedBridge.log("flyme助手: 检测完到更新包");
                    XposedBridge.log(update);
                } else {
                    //XposedBridge.log("获取Context失败0x0");
                }
            } else {
                //XposedBridge.log("flyme助手: 检测完到更新包已被记录");
            }
        }
        return needToast;
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

}
