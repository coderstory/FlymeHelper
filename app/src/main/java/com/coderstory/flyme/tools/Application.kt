package com.coderstory.flyme.tools

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        com.coderstory.flyme.tools.Application.Companion.mInstance = this
        System.loadLibrary("Utils")
        Cpp.firstCpp(com.coderstory.flyme.tools.Application.Companion.mInstance)
    }

    companion object {
        private val mInstance: com.coderstory.flyme.tools.Application? = null
        val instance: com.coderstory.flyme.tools.Application
            get() = com.coderstory.flyme.tools.Application.Companion.mInstance
    }
}