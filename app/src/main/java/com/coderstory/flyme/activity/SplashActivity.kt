package com.coderstory.flyme.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.coderstory.flyme.R
import com.coderstory.flyme.activity.MainActivity
import com.coderstory.flyme.tools.Cpp

class SplashActivity : Activity() {
    override fun getResources(): Resources { //还原字体大小
        val res = super.getResources()
        //非默认值
        if (res.configuration.fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        setContentView(R.layout.activity_splash)
        //倒计时返回主界面
        object : AsyncTask<Void?, Void?, Int?>() {
            override fun onPostExecute(result: Int?) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun doInBackground(vararg params: Void?): Int {
                try {
                    Thread.sleep(SHOW_TIME_MIN.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                Cpp.runTest()
                return 1
            }

        }.execute()
    }

    companion object {
        private const val SHOW_TIME_MIN = 1200
    }
}