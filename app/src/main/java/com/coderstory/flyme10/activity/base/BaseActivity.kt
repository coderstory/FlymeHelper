package com.coderstory.flyme10.activity.base

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(setLayoutResourceID())
        setUpView()
        setUpData()
    }

    protected open fun setUpData() {}

    /***
     * 用于在初始化View之前做一些事
     */
    protected open fun init() {}
    protected abstract fun setUpView()
    protected abstract fun setLayoutResourceID(): Int
    protected fun <T : View?> `$`(id: Int): T {
        return super.findViewById<View>(id) as T
    }

    protected fun startActivityWithoutExtras(clazz: Class<*>?) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun getResources(): Resources {
        //还原字体大小
        val res = super.getResources()
        //非默认值
        if (res.configuration.fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults()
            //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }
}