package com.coderstory.purify.module;

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
            // 检测类型改成每夜版
//            findAndHookMethod("com.meizu.flyme.update.network.RequestManager", loadPackageParam.classLoader, "getSystemUpgradeSwitchParams", String.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    param.args[0] = "daily";
//                }
//            });

            // 替换检测更新接口参数 当前系统类型 daily beta stable
//            findAndHookMethod("com.meizu.flyme.update.network.RequestManager", loadPackageParam.classLoader, "generateSysParam", String.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    param.args[0] = "daily";
//                }
//            });
            // 替换检测更新接口参数  屏蔽检测 root
            //findAndHookMethod("com.meizu.flyme.update.common.d.b", loadPackageParam.classLoader, "i", Context.class, XC_MethodReplacement.returnConstant("0"));
            // 替换检测更新接口参数  当前系统版本
//            findAndHookMethod("com.meizu.flyme.update.common.d.b", loadPackageParam.classLoader, "c", Context.class, XC_MethodReplacement.returnConstant("8.1.0-1541448550_stable"));
//
//            findAndHookMethod("com.meizu.flyme.update.model.i$a", loadPackageParam.classLoader, "getTargetFirmwareType", XC_MethodReplacement.returnConstant("daily"));
//
//

            hookAllMethods("com.meizu.flyme.update.i.e", loadPackageParam.classLoader, "a", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    for (String s : param.args[0].toString().split("\"")) {
                        XposedBridge.log(s);
                    }

                }
            });
        }

    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

}
