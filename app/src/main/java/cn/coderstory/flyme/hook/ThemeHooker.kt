package cn.coderstory.flyme.hook

import android.content.Context
import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

/**
 * 主题无限制试用和混搭付费主题
 */
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
                    param(String::class.java, String::class.java, Int::class.java, Int::class.java)
                }
                intercept()
            }
        }

        // 拦截开机自启广播
        "com.meizu.customizecenter.admin.receiver.BootBroadcastReceiver".hook {
            injectMember {
                method { name = "onReceive" }
                intercept()
            }
        }

        // 拦截字体试用服务
        "com.meizu.customizecenter.manager.managermoduls.font.FontTrialService".hook {
            injectMember {
                method { name = "onStartCommand" }
                replaceTo(2)
            }
        }
        // 拦截主题试用服务
        "com.meizu.customizecenter.manager.managermoduls.theme.ThemeTrialService".hook {
            injectMember {
                method { name = "onStartCommand" }
                replaceTo(2)
            }
        }
        // resetToSystemTheme
        "com.meizu.flyme.policy.sdk.pe0".hook {
            injectMember {
                method { name = "J" }
                replaceToTrue()
            }
        }



        /**
         * public void a(boolean arg4, boolean arg5) {
         * com.meizu.customizecenter.manager.utilstool.c.b.b(this.b, "startFontRestoreService");
         * Intent v0 = new Intent(this.d, FontRestoreService.class);
         * v0.putExtra(com.meizu.customizecenter.model.a.a$g.h.a(), this.e().a());
         * v0.putExtra("is_go_to_pay_font", arg5);
         * v0.putExtra("is_restore_last_key", arg4);
         * com.meizu.customizecenter.manager.utilstool.systemutills.a.a.a(this.d, v0);
         * }
         * */
        "com.meizu.customizecenter.manager.managermoduls.font.k".hook {

            injectMember {
                method {
                    name = "Y"
                    param(Boolean::class.java, Boolean::class.java)
                }
                intercept()
            }
        }

        // "checkTrialFont:!isUsingTrialFont() Context context, String str, long j
        "com.meizu.customizecenter.manager.managermoduls.font.k".hook {
            injectMember {
                method {
                    name = "l"
                    param(Context::class.java, String::class.java, Long::class.java)
                }
                intercept()
            }
        }

        "com.meizu.customizecenter.manager.utilshelper.restorehelper.ThemeRestoreService".hook {
            injectMember {
                method { name = "onStartCommand" }
                replaceTo(0)
            }
        }

        "com.meizu.customizecenter.manager.utilshelper.restorehelper.FontRestoreService".hook {
            injectMember {
                method { name = "onStartCommand" }
                replaceTo(2)
            }
        }
        "com.meizu.customizecenter.manager.utilshelper.scannerhelper.CustomizeScannerService".hook {
            injectMember {
                method { name = "onStartCommand" }
                replaceTo(2)
            }
        }


    }
}