package cn.coderstory.flyme.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE

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