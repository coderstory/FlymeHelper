package com.coderstory.flyme.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.hostshelper.FileHelper;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;

public class XposedFragment extends BaseFragment {
    @SuppressLint("HandlerLeak")
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
            switch (msg.arg1) {
                case 0:
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("安装完毕,重启生效");
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
        $(R.id.install_magisk_module_riru).setOnClickListener(v -> {
            if (!installMagisk("magisk-riru-v21.3.zip", "Riru安装日志")) {
                Toast.makeText(getMContext(), "riru安装失败", Toast.LENGTH_SHORT).show();
            }
        });
        $(R.id.install_magisk_module_y).setOnClickListener(v -> {
            if (installMagisk("EdXposed-YAHFA-v0.4.6.2.4529.-release.zip", "EdXposed安装日志")) {
                Toast.makeText(getMContext(), "安装成功 重启生效", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getMContext(), "Edxposed-y安装失败", Toast.LENGTH_SHORT).show();
            }
        });

        $(R.id.install_magisk_module_s).setOnClickListener(v -> {

            if (installMagisk("EdXposed-SandHook-v0.4.6.2.4529.-release.zip", "EdXposed安装日志")) {
                Toast.makeText(getMContext(), "安装成功 重启生效", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getMContext(), "Edxposed-s安装失败", Toast.LENGTH_SHORT).show();
            }
        });


        $(R.id.install_module_y).setOnClickListener(v -> {
            installByCopy("EdXposedY-v0.4.6.2.4529.zip");
        });
        $(R.id.install_module_s).setOnClickListener(v -> {
            installByCopy("EdXposedS-v0.4.6.2.4529.zip");
        });

    }

    private boolean installMagisk(String fileName, String moduleName) {
        String base = getMContext().getFilesDir().getAbsolutePath();
        FileHelper.saveAssets(getMContext(), fileName, base);
        FileHelper.saveAssets(getMContext(), "installer", base);
        List<String> commands = new ArrayList<>();
        Shell.su("chmod 777 " + base + "/installer").exec();
        Shell.su("dos2unix " + base + "/installer").exec();
        List<String> result = Shell.su("sh " + base + "/installer dummy 1 " + base + "/" + fileName).exec().getOut();

        Layer anyLayer = AnyLayer.dialog(getMContext())
                .contentView(R.layout.dialog_def)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick((AnyLayer, v) -> {
                    AnyLayer.dismiss();
                }, R.id.tv_close);
        anyLayer.show();
        CardView cardView = (CardView) ((DialogLayer) anyLayer).getContentView();
        LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
        TextView textView = (TextView) linearLayout.getChildAt(0);
        boolean resultB = result.size() > 5 && "- Done".equals(result.get(result.size() - 1));
        result = result.stream().filter(item -> !item.startsWith("***") &&
                !item.startsWith("mount") &&
                !item.startsWith("Archive:") &&
                !item.startsWith("  inflating:"))
                .collect(Collectors.toList());
        textView.setText(Html.fromHtml(result.stream().reduce(moduleName + "<br>", (a, b) -> a + "<br>" + b) + "<br><br>" + (resultB ? "<font color='#dd2c00'><storage>!!安装成功,重启生效!!</b></font><br>" : "<font color='#dd2c00'><b>!!安装失败!!</b></font><br>")));

        return resultB;
    }

    private void installByCopy(String fileName) {
        Toast.makeText(getMContext(), "正在安装,请稍后。。", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            // /sbin/.magisk/mirror/system_root
            String systemRoot = "/system";
            String base = getMContext().getFilesDir().getAbsolutePath();
            Shell.su("rm -rf " + base + "/data");
            Shell.su("rm -rf " + base + "/system");
            FileHelper.UnZipAssetsFolder(getMContext(), fileName, base);
            if (run("mount -o rw,remount " + systemRoot)) {
                com.topjohnwu.superuser.Shell.su("cp -rf " + base + "/data/* /data").exec();
                com.topjohnwu.superuser.Shell.su("cp -rf " + base + "/system/* " + systemRoot).exec();

                List<String> commands = new ArrayList<>();
                commands.add("chmod 0644 /system/lib/libriru_edxp.so");
                commands.add("chmod 0644 /system/lib/libmemtrack.so");
                commands.add("chmod 0644 /system/lib/libwhale.edxp.so");
                commands.add("chmod 0644 /system/lib/libsandhook.edxp.so");
                commands.add("chmod 0644 /system/lib64/libriru_edxp.so");
                commands.add("chmod 0644 /system/lib64/libmemtrack.so");
                commands.add("chmod 0644 /system/lib64/libwhale.edxp.so");
                commands.add("chmod 0644 /system/lib64/libsandhook.edxp.so");
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
            }


        }).start();
    }

    public boolean run(String command) {
        Shell.Result result = Shell.su(command).exec();
        if (result.getCode() != 0) {
            Message msg = new Message();
            msg.arg1 = 1;
            msg.obj = result.getOut().stream().reduce("", (a, b) -> a + b);
            myHandler.sendMessage(msg);
            return false;
        }
        return true;

    }
}