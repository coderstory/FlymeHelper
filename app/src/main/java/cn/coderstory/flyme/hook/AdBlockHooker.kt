package cn.coderstory.flyme.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE

class AdBlockHooker : YukiBaseHooker() {
    override fun onHook() {
        findClass("com.meizu.advertise.api.AdManager").runCatching {
            this.hook {
                injectMember {
                    method {
                        name {
                            val methods = listOf("installPlugin", "install", "init")
                            methods.contains(it)
                        }
                    }.all()
                    intercept()
                }.onAllFailure {
                    loggerE(msg = "Hook Browser fail: ${it.message}")
                }
            }
        }.exceptionOrNull()?.let {
            loggerE(msg = "Hook Browser fail: ${it.message}")
        }

        findClass("com.meizu.advertise.update.PluginManager").hook {
            injectMember {
                method {
                    name {
                        val methods = listOf("isFirstInstalled", "newContext")
                        methods.contains(it)
                    }
                }.all()
                replaceToTrue()
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }

            injectMember {
                method {
                    name {
                        val methods = listOf("install", "installFromDownload")
                        methods.contains(it)
                    }
                }.all()
                intercept()
            }.onAllFailure {
                loggerE(msg = "Hook Browser fail: ${it.message}")
            }
        }

    }
}