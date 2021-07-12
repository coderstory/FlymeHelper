package com.coderstory.flyme.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment
import com.coderstory.flyme.tools.hostshelper.FileHelper
import com.topjohnwu.superuser.Shell
import java.io.File
import java.util.*
import java.util.function.Consumer

class XposedFragment : BaseFragment() {
    @SuppressLint("HandlerLeak")
    var myHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val normalDialog = AlertDialog.Builder(mContext)
            when (msg.arg1) {
                0 -> {
                    normalDialog.setTitle("提示")
                    normalDialog.setMessage("框架安装完毕,重启生效")
                    normalDialog.setPositiveButton("确定"
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                    normalDialog.show()
                    super.handleMessage(msg)
                }
                1 -> {
                    normalDialog.setTitle("框架安装失败")
                    normalDialog.setMessage("命令执行失败，返回值:" + msg.obj)
                    normalDialog.setPositiveButton("确定"
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                    normalDialog.show()
                    super.handleMessage(msg)
                }
            }
        }
    }

    public override fun setLayoutResourceID(): Int {
        return R.layout.fragment_xposed
    }

    public override fun setUpView() {
        if (Build.VERSION.SDK_INT == 30) {
            val normalDialog = AlertDialog.Builder(mContext)
            normalDialog.setTitle("很遗憾")
            normalDialog.setMessage("检测到你正在使用基于Android 11的安卓系统,无法通过ROOT安装EdXposed框架")
            normalDialog.setNegativeButton("确定"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            normalDialog.show()
        } else {
            `$`<View>(R.id.install_magisk_module_riru).setOnClickListener { v: View? -> installByCopy("magisk-riru-v21.3.zip") }
            `$`<View>(R.id.install_magisk_module_y).setOnClickListener { v: View? ->
                if (checkRiru()) {
                    installByCopy("magisk-riru-storage-redirect-v22.8.zip")
                }
            }
            `$`<View>(R.id.install_module_y).setOnClickListener { v: View? ->
                if (checkRiru() && File("/system/lib/libriru_edxp.so").exists()) {
                    val normalDialog = AlertDialog.Builder(mContext)
                    normalDialog.setTitle("提示")
                    normalDialog.setMessage("检测到已经安装xposed框架,覆盖安装可能导致无法开机")
                    normalDialog.setPositiveButton("继续安装"
                    ) { dialog: DialogInterface?, which: Int -> installByCopy("EdXposed-YAHFA-vDEVTESTONLY.4631-release.zip") }
                    normalDialog.setNegativeButton("取消安装"
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                    normalDialog.show()
                } else {
                    installByCopy("EdXposed-YAHFA-vDEVTESTONLY.4631-release.zip")
                }
            }
        }
    }

    private fun checkRiru(): Boolean {
        return if (!File("/system/lib64/libmemtrack.so.sha256sum").exists()) {
            val normalDialog = AlertDialog.Builder(mContext)
            normalDialog.setTitle("提示")
            normalDialog.setMessage("检测到尚未安装Riru模块,请安装后再试")
            normalDialog.setNegativeButton("确定"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            normalDialog.show()
            false
        } else {
            true
        }
    }

    private fun installByCopy(fileName: String) {
        Toast.makeText(mContext, "正在安装,请稍后。。", Toast.LENGTH_SHORT).show()
        Thread {

            // /sbin/.magisk/mirror/system_root
            val systemRoot = "/system"
            val base = mContext.filesDir.absolutePath
            Shell.su("rm -rf $base/data").exec()
            Shell.su("rm -rf $base/system").exec()
            FileHelper.Companion.UnZipAssetsFolder(mContext, fileName, base)
            //if (run("mount -o rw,remount " + systemRoot)) {
            Shell.su("mount -o rw,remount $systemRoot").exec()
            Shell.su("mount -o rw,remount /").exec()
            Shell.su("cp -rf $base/data/* /data").exec()
            Shell.su("cp -rf $base/system/* $systemRoot").exec()
            val commands: MutableList<String> = ArrayList()
            commands.add("chmod 0644 /system/lib/libriru_edxp.so")
            commands.add("chmod 0644 /system/lib/libmemtrack.so")
            commands.add("chmod 0644 /system/lib/libwhale.edxp.so")
            commands.add("chmod 0644 /system/lib/libsandhook.edxp.so")
            commands.add("chmod 0644 /system/lib/libriru_storage_redirect.so")
            commands.add("chmod 0644 /system/lib64/libriru_edxp.so")
            commands.add("chmod 0644 /system/lib64/libmemtrack.so")
            commands.add("chmod 0644 /system/lib64/libwhale.edxp.so")
            commands.add("chmod 0644 /system/lib64/libsandhook.edxp.so")
            commands.add("chmod 0644 /system/lib64/libriru_storage_redirect.so")
            commands.add("chmod 0644 /system/framework/ed*")
            commands.add("chmod -R 0755 /data/misc/riru")
            commands.add("chmod 0700 /data/misc/riru/bin/zygote_restart")
            commands.add("chmod 0644 /data/misc/riru/modules/edxp/module.prop")
            commands.forEach(Consumer { c: String? -> Shell.su(c).exec() })
            if (!prefs.getBoolean("alreadyWriteProp", false)) {
                Shell.su("echo dalvik.vm.dex2oat-filter=quicken >> /system/build.prop").exec()
                Shell.su("echo dalvik.vm.dex2oat-flags=--inline-max-code-units=0 >> /system/build.prop").exec()
                Shell.su("echo dalvik.vm.image-dex2oat-flags=--inline-max-code-units=0 --compiler-filter=speed >> /system/build.prop").exec()
                editor.putBoolean("alreadyWriteProp", true)
            }
            val msg = Message()
            msg.arg1 = 0
            myHandler.sendMessage(msg)
        }.start()
    }
}