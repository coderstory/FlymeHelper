package cn.coderstory.flyme.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE

/**
 * 去除魅族天气App的广告
 * hook代码标志字符串
 * ```
 * meizu_18
 * ```
 *
 * TODO 因为方法混淆 hook点需要动态匹配
 */
class WeatherHooker : YukiBaseHooker() {
    override fun onHook() {
        "com.common.advertise.plugin.utils.f".hook {
            injectMember {
                method {
                    name = "p"
                }
                replaceToTrue()
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }

        "com.meizu.flyme.policy.sdk.yw\"".hook {
            injectMember {
                method {
                    name = "a"
                }
                replaceToTrue()
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }
    }

}