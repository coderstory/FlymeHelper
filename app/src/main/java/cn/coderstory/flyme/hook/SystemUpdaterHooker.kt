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
        var mContext: Context? = null
        "com.meizu.flyme.update.network.RequestManager".hook {
            // 这个hook是为了拿到一个 Context 用于弹出一个Toast
            injectMember {
                method { constructor() }
                afterHook {
                    if (this.args[0] is Context) {
                        mContext = this.args[0] as Context
                    }
                }

            }.onAllFailure { loggerE(msg = "Hook Browser fail: ${it.message}") }
        }

        /**
         * 这个hook类是获取更新包信息反序列化后的实体类
         * TODO 这个类型的查找需要改进
         * ```
         * public class k {
         *          public b cdnCheckResult;
         *          public e currentFimware;
         *          public g firmwarePlan;
         *          public UpgradeFirmware upgradeFirmware;
         *          public k(UpgradeFirmware upgradeFirmware, e eVar, g gVar, b bVar) {
         *              this.upgradeFirmware = upgradeFirmware;
         *              this.currentFimware = eVar;
         *              this.firmwarePlan = gVar;
         *              this.cdnCheckResult = bVar;
         *          }
         *      }
         * ```
         */
        "com.meizu.flyme.update.model.h".hook {
            injectMember {
                method { constructor() }
                afterHook {
                    val obj = this.instance
                    val currentFirmware = XposedHelpers.getObjectField(obj, "currentFimware")
                    handleInfo(currentFirmware, mContext)
                    val upgradeFirmware = XposedHelpers.getObjectField(obj, "upgradeFirmware")
                    handleInfo(upgradeFirmware, mContext)
                }
            }.onAllFailure { loggerE(msg = "Hook Browser fail: ${it.message}") }
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
                prefs.edit().putString("updateList", Base64.encodeToString(update.toByteArray(), Base64.DEFAULT))
                Toast.makeText(mContext, "flyme助手:已检测到新的更新包地址", Toast.LENGTH_LONG).show()
                XposedBridge.log(update)
            }
        }
    }
}