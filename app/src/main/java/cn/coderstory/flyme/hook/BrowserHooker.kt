package cn.coderstory.flyme.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE

@InjectYukiHookWithXposed(isUsingResourcesHook = true)
class BrowserHooker : YukiBaseHooker() {
    override fun onHook() {
        "com.common.advertise.plugin.utils.DeviceUtils".hook {
            injectMember {
                method {
                    name = "is18Device"
                }
                replaceToTrue()
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }

        "com.meizu.advertise.admediation.i.a".hook {
            injectMember {
                method {
                    name = "a"
                }
                replaceToTrue()
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }

        "com.meizu.advertise.admediation.c.e".hook {
            injectMember {
                method {
                    name = "a"
                }
                replaceTo(null)
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }
    }
}