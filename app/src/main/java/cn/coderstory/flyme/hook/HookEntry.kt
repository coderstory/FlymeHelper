package cn.coderstory.flyme.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "flymehelper"
        }
    }

    override fun onHook() = encase {
        if (prefs.getBoolean("enable_browser_block_ad", false)) {
            loadApp("com.android.browser", BrowserHooker())
        }
    }
}