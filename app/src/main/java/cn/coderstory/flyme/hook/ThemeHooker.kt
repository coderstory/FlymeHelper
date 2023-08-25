package cn.coderstory.flyme.hook

import android.content.Context
import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class ThemeHooker : YukiBaseHooker() {
    override fun onHook() {
        resources().hook {
            injectResource {
                conditions {
                    resourceId = 0x7f110338
                    string()
                }
                replaceTo("开始白嫖")
            }
            injectResource {
                conditions {
                    resourceId = 0x7f11033f
                    string()
                }
                replaceTo("开始白嫖")
            }
            injectResource {
                conditions {
                    resourceId = 0x7f110345
                    string()
                }
                replaceTo("开始白嫖")
            }
        }

        // 开始试用主题
        "com.meizu.customizecenter.manager.managermoduls.theme.f".hook {
            injectMember {
                method {
                    name = "N"
                    paramCount = 4
                    param(
                        String::class.java,
                        String::class.java,
                        Int::class.java,
                        Int::class.java
                    )
                }
                intercept()
            }
        }

        // 拦截开机自启广播
        "com.meizu.customizecenter.admin.receiver.BootBroadcastReceiver".hook {
            injectMember {
                method {
                    name = "onReceive"
                    paramCount = 2
                    param(
                        Context::class.java,
                        Intent::class.java
                    )
                }
                intercept()
            }
        }

        // 拦截试用服务
        "com.meizu.customizecenter.manager.managermoduls.font.FontTrialService".hook {
            injectMember {
                method {
                    name = "onStartCommand"
                    param(
                        Context::class.java,
                        Intent::class.java
                    )
                }
                replaceTo(2)
            }
        }

        "com.meizu.customizecenter.manager.managermoduls.theme.ThemeTrialService".hook {
            injectMember {
                method {
                    name = "onStartCommand"
                    param(
                        Context::class.java,
                        Intent::class.java
                    )
                }
                replaceTo(2)
            }
        }

        "com.meizu.flyme.policy.sdk.pe0".hook {
            injectMember {
                method {
                    name = "J"
                }
                replaceToTrue()
            }
        }

        "com.meizu.customizecenter.manager.managermoduls.font.k".hook {

            injectMember {
                method {
                    name = "Y"
                    param(
                        Boolean::class.java,
                        Boolean::class.java
                    )
                }
                intercept()
            }
        }

        "com.meizu.customizecenter.manager.managermoduls.font.k".hook {

            injectMember {
                method {
                    name = "l"
                    param(
                        Context::class.java,
                        String::class.java,
                        Long::class.java
                    )
                }
                intercept()
            }
        }

        "com.meizu.customizecenter.manager.utilshelper.restorehelper.ThemeRestoreService".hook {
            injectMember {
                method {
                    name = "onStartCommand"
                }
                replaceTo(0)
            }
        }

        "com.meizu.customizecenter.manager.utilshelper.restorehelper.FontRestoreService".hook {
            injectMember {
                method {
                    name = "onStartCommand"
                }
                replaceTo(2)
            }
        }
        "com.meizu.customizecenter.manager.utilshelper.scannerhelper.CustomizeScannerService".hook {
            injectMember {
                method {
                    name = "onStartCommand"
                }
                replaceTo(2)
            }
        }



    }
}