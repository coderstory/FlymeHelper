package cn.coderstory.flyme.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

/**
 * Hook入口类
 */
@InjectYukiHookWithXposed(isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "flymehelper"
        }
    }

    override fun onHook() = encase {
        if (prefs.getBoolean("enable_block_ad", false)) {

            if ("com.android.browser" == packageName) {
                loadApp(packageName, BrowserHooker())
            }
            if ("com.meizu.flyme.weather" == packageName) {
                loadApp(packageName, WeatherHooker())
            }
            if (packageName.contains("meizu") || packageName.contains("flyme") || packageName.contains("mz")) {
                loadApp(packageName, AdBlockHooker())
            }
            if ("com.android.packageinstaller" == packageName) {
                loadApp(packageName, PkgInstallerHooker())
            }

            if("com.android.systemui" == packageName){
                loadApp(packageName, SystemUIHooker())
            }

            if("com.meizu.flyme.update" == packageName){
                loadApp(packageName, SystemUpdaterHooker())
            }

            if("com.meizu.customizecenter" == packageName){
                loadApp(packageName,ThemeHooker())
            }

        }

    }
}