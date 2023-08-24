package cn.coderstory.flyme.hook

import android.content.Context
import android.util.Base64
import android.widget.Toast
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class SystemUpdaterHooker : YukiBaseHooker() {
    override fun onHook() {
        "com.meizu.flyme.update.network.RequestManager".hook {
            var  mContext: Context? = null
            injectMember {
                method {
                    constructor()
                }
                afterHook {
                    if (this.args[0] is Context) {
                        mContext = this.args[0] as Context
                    }
                }

            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }

            injectMember {
                method {
                    constructor()
                }
                afterHook {
                    val obj = this.instance
                    val currentFirmware = XposedHelpers.getObjectField(obj, "currentFimware")
                    handleInfo(currentFirmware, mContext)
                    val upgradeFirmware = XposedHelpers.getObjectField(obj, "upgradeFirmware")
                    handleInfo(upgradeFirmware, mContext)
                }

            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }
    }

    private fun handleInfo(info: Any?, mContext: Context?) {
        if (info != null) {
            var update = prefs.getString("updateList", "")
            val systemVersion = XposedHelpers.getObjectField(info, "systemVersion") as String
            val updateUrl = XposedHelpers.getObjectField(info, "updateUrl") as String
            val releaseDate = XposedHelpers.getObjectField(info, "releaseDate") as String
            val fileSize = XposedHelpers.getObjectField(info, "fileSize") as String
            val msg = "$systemVersion@$updateUrl@$fileSize@$releaseDate"
            if (!update.contains(msg)) {
                update += "$msg;"
                prefs.edit().putString(
                    "updateList",
                    Base64.encodeToString(update.toByteArray(), Base64.DEFAULT)
                )
                Toast.makeText(mContext, "flyme助手:已检测到新的更新包地址", Toast.LENGTH_LONG)
                    .show()
                XposedBridge.log(update)
            }
        }
    }
}