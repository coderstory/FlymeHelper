package cn.coderstory.flyme.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE
import de.robv.android.xposed.XposedHelpers

/**
 * com.android.packageinstaller
 */
class PkgInstallerHooker : YukiBaseHooker() {
    override fun onHook() {
        // 禁止关联商店
        "com.meizu.safe.security.net.HttpMethods".hook {
            injectMember {
                method {
                    name = "queryPackageInfoFromMzStoreV2"
                }
                beforeHook {
                    this.args[0] = "xxx"
                    this.args[3] = "xxx"
                    this.args[6] = "xxx"
                }
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }

        // 自动安装apk
        "com.meizu.permissioncommon.AppInfoUtil".hook {
            injectMember {
                method {
                    name = "isSystemApp"
                }
                replaceToTrue()
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }

        // 禁止安装app时候的安全检验
        "com.android.packageinstaller.FlymePackageInstallerActivity".hook {
            injectMember {
                method {
                    name = "setVirusCheckTime"
                }
                replaceAny {
                    val mHandler = XposedHelpers.getObjectField(this.instance, "mHandler")
                    XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 5)
                }
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }

            injectMember {
                method {
                    name = "replaceOrInstall"
                }
                beforeHook {
                    XposedHelpers.setObjectField(this.instance, "mAppInfo", null)
                }
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }

        "com.meizu.safe.security.utils.Utils".hook {
            injectMember {
                method {
                    name = "isCtsRunning"
                }
                replaceToTrue()
            }
        }


    }
}