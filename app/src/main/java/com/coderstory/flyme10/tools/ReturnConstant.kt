package com.coderstory.flyme10.tools

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences

class ReturnConstant(
    private val prefs: XSharedPreferences,
    private val prefsKey: String,
    private val value: Any?
) : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        super.beforeHookedMethod(param)
        prefs.reload()
        if (prefs.getBoolean(prefsKey, true)) {
            param.result = value
        }
    }
}