package com.coderstory.flyme.patchModule

import android.content.Context
import android.util.Base64
import android.widget.Toast
import com.coderstory.flyme.patchModule.Others
import com.coderstory.flyme.tools.SharedHelper
import com.coderstory.flyme.tools.XposedHelper
import com.coderstory.flyme.xposed.IModule
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Others : XposedHelper(), IModule {
    override fun handleInitPackageResources(respray: InitPackageResourcesParam) {}
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        // 禁止安装app时候的安全检验
        if (loadPackageParam.packageName == "com.android.packageinstaller") {
            if (prefs.getBoolean("enableCheckInstaller", false)) {
                // 8.x
                val clazz: Class<*> = XposedHelper.Companion.findClass("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader)
                XposedHelper.Companion.findAndHookMethod(clazz, "setVirusCheckTime", object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        val mHandler = XposedHelpers.getObjectField(param.thisObject, "mHandler")
                        XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 5)
                    }
                })
                XposedHelper.Companion.findAndHookMethod("com.android.packageinstaller.FlymePackageInstallerActivity", loadPackageParam.classLoader, "replaceOrInstall", String::class.java, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        super.beforeHookedMethod(param)
                        XposedHelpers.setObjectField(param.thisObject, "mAppInfo", null)
                    }
                })
            }
            if (prefs.getBoolean("enableCTS", false)) {
                //XposedBridge.log("开启原生安装器");
                XposedHelper.Companion.findAndHookMethod("com.meizu.safe.security.utils.Utils", loadPackageParam.classLoader, "isCtsRunning", XC_MethodReplacement.returnConstant(true))
            }
        }
        if (loadPackageParam.packageName == "com.meizu.flyme.update") {

            // 获取Context
            // public abstract class a<T> implements ErrorListener, Listener {
            //     public a(Context context) {
            //        this.b = context.getApplicationContext();
            //        this.c = RequestManager.getInstance(this.b);
            //    }
            XposedBridge.hookAllConstructors(XposedHelper.Companion.findClass("com.meizu.flyme.update.c.a", loadPackageParam.classLoader), object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    if (param.args[0] is Context) {
                        Others.Companion.mContext = param.args[0] as Context
                    }
                }
            })
            XposedBridge.hookAllConstructors(XposedHelper.Companion.findClass("com.meizu.flyme.update.d.a", loadPackageParam.classLoader), object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    if (param.args[0] is Context) {
                        Others.Companion.mContext = param.args[0] as Context
                    }
                }
            })

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
            XposedBridge.hookAllConstructors(XposedHelper.Companion.findClass("com.meizu.flyme.update.model.n", loadPackageParam.classLoader), object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val obj = param.thisObject
                    val currentFimware = XposedHelpers.getObjectField(obj, "currentFimware")
                    handleInfo(currentFimware)
                    val upgradeFirmware = XposedHelpers.getObjectField(obj, "upgradeFirmware")
                    handleInfo(upgradeFirmware)
                }
            })
        }
    }

    private fun handleInfo(info: Any?) {
        var needToast = false
        if (info != null) {
            var update = SharedHelper(Others.Companion.mContext!!).getString("updateList", "")
            // update = new String(android.util.Base64.decode(update, Base64.DEFAULT));
            val systemVersion = XposedHelpers.getObjectField(info, "systemVersion") as String
            val updateUrl = XposedHelpers.getObjectField(info, "updateUrl") as String
            val releaseDate = XposedHelpers.getObjectField(info, "releaseDate") as String
            val fileSize = XposedHelpers.getObjectField(info, "fileSize") as String
            val msg = "$systemVersion@$updateUrl@$fileSize@$releaseDate"
            if (!update.contains(msg)) {
                needToast = true
                update += "$msg;"
                if (Others.Companion.mContext != null) {
                    XposedBridge.log("参数保存结果" + SharedHelper(Others.Companion.mContext!!).put("updateList", Base64.encodeToString(update.toByteArray(), Base64.DEFAULT)))
                    Toast.makeText(Others.Companion.mContext, "flyme助手:已检测到新的更新包地址", Toast.LENGTH_LONG).show()
                    //XposedBridge.log("flyme助手: 检测完到更新包");
                    XposedBridge.log(update)
                } else {
                    //XposedBridge.log("获取Context失败0x0");
                }
            } else {
                //XposedBridge.log("flyme助手: 检测完到更新包已被记录");
            }
        }
    }

    override fun initZygote(startupParam: StartupParam?) {}

    companion object {
        private var mContext: Context? = null
    }
}