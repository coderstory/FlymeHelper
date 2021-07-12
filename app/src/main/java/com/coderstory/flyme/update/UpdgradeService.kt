package com.coderstory.flyme.update

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import com.coderstory.flyme.R
import com.coderstory.flyme.tools.SharedHelper
import com.itsnows.upgrade.UpgradeManager
import com.itsnows.upgrade.model.bean.UpgradeOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UpdgradeService(private val mActivity: Activity) {
    private val helper: SharedHelper = SharedHelper(mActivity)
    fun checkUpgrade() {
        val time = helper.getString("last_update_check_time", "")
        val now = Base64.encodeToString(SimpleDateFormat("yyyyMMddHH").format(Date()).toByteArray(), Base64.DEFAULT)
        if (time != "" && now == Base64.encodeToString(time.toByteArray(), Base64.DEFAULT)) {
            return
        } else {
            helper.put("last_update_check_time", now)
        }
        val manager = UpgradeManager(mActivity)
        // 自动检测更新
        manager.checkForUpdates(UpgradeOptions.Builder() // 对话框主题（可选）
                //.setTheme(ContextCompat.getColor(mActivity, R.color.colorPrimary))
                // 通知栏图标（可选）
                .setIcon(BitmapFactory.decodeResource(mActivity.resources, R.mipmap.ic_launcher)) // 通知栏标题（可选）
                .setTitle("正在下载更新包") // 通知栏描述（可选）
                .setDescription("更新通知栏") // 下载链接或更新文档链接
                .setUrl("https://app-1301652864.cos.ap-nanjing.myqcloud.com/update.json") // 下载文件存储路径（可选）
                .setStorage(File(Environment.getExternalStorageDirectory().absolutePath + "/Download/flymeHelper.upgrade.apk")) // 是否支持多线性下载（可选）
                .setMultithreadEnabled(true) // 线程池大小（可选）
                .setMultithreadPools(10) // 文件MD5（可选）
                .setMd5(null) // 是否自动删除安装包（可选）
                .setAutocleanEnabled(true) // 是否自动安装安装包（可选）
                .setAutomountEnabled(true) // 是否自动检测更新
                .build(), true)
    }

}