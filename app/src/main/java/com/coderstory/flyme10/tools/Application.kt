package com.coderstory.flyme10.tools

import android.app.Application
import com.umeng.commonsdk.UMConfigure

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        UMConfigure.setLogEnabled(true)
        UMConfigure.preInit(applicationContext, "5ee5d80f978eea081640e210", "weibo")
        UMConfigure.init(
            this,
            "5ee5d80f978eea081640e210",
            "weibo",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
    }
}