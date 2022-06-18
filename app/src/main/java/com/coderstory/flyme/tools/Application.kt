package com.coderstory.flyme.tools

import android.app.Application
import android.content.Context

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        System.loadLibrary("Utils")

    }

    companion object {
        private lateinit var mInstance: Context
        val instance: Context
            get() = mInstance
    }
}