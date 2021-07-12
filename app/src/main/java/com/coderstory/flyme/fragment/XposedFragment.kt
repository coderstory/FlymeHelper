package com.coderstory.flyme.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.tools.hostshelper.FileHelper;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XposedFragment extends BaseFragment {
    @SuppressLint("HandlerLeak")
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
            switch (msg.arg1) {
                case 0:
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("框架安装完毕,重启生效");
                    normalDialog.setPositiveButton("确定",
                            (dialog, which) -> dialog.dismiss());
                    normalDialog.show();
                    super.handleMessage(msg);
                    break;
                case 1:
                    normalDialog.setTitle("框架安装失败");
                    normalDialog.setMessage("命令执行失败，返回值:" + msg.obj);
                    normalDialog.setPositiveButton("确定",
                            (dialog, which) -> dialog.dismiss());
                    normalDialog.show();
                    super.handleMessage(msg);
                    break;
            }
        }
    };


    @Override
    public int setLayoutResourceID() {
        return R.layout.fragment_xposed;
    }

    @Override
    public void setUpView() {
        if (android.os.Build.VERSION.SDK_INT == 30) {
            androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
            normalDialog.setTitle("很遗憾");
            normalDialog.setMessage("检测到你正在使用基于Android 11的安卓系统,无法通过ROOT安装EdXposed框架");
            normalDialog.setNegativeButton("确定",
                    (dialog, which) -> dialog.dismiss());
            normalDialog.show();
        } else {
            $(R.id.install_magisk_module_riru).setOnClickListener(v -> {
                installByCopy("magisk-riru-v21.3.zip");
            });
            $(R.id.install_magisk_module_y).setOnClickListener(v -> {
                if (checkRiru()) {
                    installByCopy("magisk-riru-storage-redirect-v22.8.zip");
                }
            });

            $(R.id.install_module_y).setOnClickListener(v -> {
                if (checkRiru() && new File("/system/lib/libriru_edxp.so").exists()) {
                    androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("检测到已经安装xposed框架,覆盖安装可能导致无法开机");
                    normalDialog.setPositiveButton("继续安装",
                            (dialog, which) -> installByCopy("EdXposed-YAHFA-vDEVTESTONLY.4631-release.zip"));
                    normalDialog.setNegativeButton("取消安装",
                            (dialog, which) -> dialog.dismiss());
                    normalDialog.show();
                } else {
                    installByCopy("EdXposed-YAHFA-vDEVTESTONLY.4631-release.zip");
                }
            });
        }
    }

    private boolean checkRiru() {
        if (!new File("/system/lib64/libmemtrack.so.sha256sum").exists()) {
            androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
            normalDialog.setTitle("提示");
            normalDialog.setMessage("检测到尚未安装Riru模块,请安装后再试");
            normalDialog.setNegativeButton("确定",
                    (dialog, which) -> dialog.dismiss());
            normalDialog.show();
            return false;
        } else {
            return true;
        }
    }


    private void installByCopy(String fileName) {
        Toast.makeText(getMContext(), "正在安装,请稍后。。", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            // /sbin/.magisk/mirror/system_root
            String systemRoot = "/system";
            String base = getMContext().getFilesDir().getAbsolutePath();
            Shell.su("rm -rf " + base + "/data").exec();
            Shell.su("rm -rf " + base + "/system").exec();
            FileHelper.UnZipAssetsFolder(getMContext(), fileName, base);
            //if (run("mount -o rw,remount " + systemRoot)) {
            com.topjohnwu.superuser.Shell.su("mount -o rw,remount " + systemRoot).exec();
            com.topjohnwu.superuser.Shell.su("mount -o rw,remount /").exec();
            com.topjohnwu.superuser.Shell.su("cp -rf " + base + "/data/* /data").exec();
            com.topjohnwu.superuser.Shell.su("cp -rf " + base + "/system/* " + systemRoot).exec();

            List<String> commands = new ArrayList<>();
            commands.add("chmod 0644 /system/lib/libriru_edxp.so");
            commands.add("chmod 0644 /system/lib/libmemtrack.so");
            commands.add("chmod 0644 /system/lib/libwhale.edxp.so");
            commands.add("chmod 0644 /system/lib/libsandhook.edxp.so");
            commands.add("chmod 0644 /system/lib/libriru_storage_redirect.so");
            commands.add("chmod 0644 /system/lib64/libriru_edxp.so");
            commands.add("chmod 0644 /system/lib64/libmemtrack.so");
            commands.add("chmod 0644 /system/lib64/libwhale.edxp.so");
            commands.add("chmod 0644 /system/lib64/libsandhook.edxp.so");
            commands.add("chmod 0644 /system/lib64/libriru_storage_redirect.so");
            commands.add("chmod 0644 /system/framework/ed*");
            commands.add("chmod -R 0755 /data/misc/riru");
            commands.add("chmod 0700 /data/misc/riru/bin/zygote_restart");
            commands.add("chmod 0644 /data/misc/riru/modules/edxp/module.prop");

            commands.forEach(c -> com.topjohnwu.superuser.Shell.su(c).exec());

            if (!getPrefs().getBoolean("alreadyWriteProp", false)) {
                com.topjohnwu.superuser.Shell.su("echo dalvik.vm.dex2oat-filter=quicken >> /system/build.prop").exec();
                com.topjohnwu.superuser.Shell.su("echo dalvik.vm.dex2oat-flags=--inline-max-code-units=0 >> /system/build.prop").exec();
                com.topjohnwu.superuser.Shell.su("echo dalvik.vm.image-dex2oat-flags=--inline-max-code-units=0 --compiler-filter=speed >> /system/build.prop").exec();
                getEditor().putBoolean("alreadyWriteProp", true);
            }
            Message msg = new Message();
            msg.arg1 = 0;
            myHandler.sendMessage(msg);
        }).start();
    }
}