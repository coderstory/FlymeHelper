package com.coderstory.flyme.fragment;

import android.text.Html;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.hostshelper.FileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;

public class XposedFragment extends BaseFragment {


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
            installByCopy("EdXposed（Y版，Riru21.3）-v0.4.6.2.4529.zip");
            Toast.makeText(getMContext(), "安装成功 重启生效", Toast.LENGTH_SHORT).show();
        });
        $(R.id.install_module_s).setOnClickListener(v -> {
            installByCopy("EdXposed（S版，Riru21.3）-v0.4.6.2.4529.zip");
            Toast.makeText(getMContext(), "安装成功 重启生效", Toast.LENGTH_SHORT).show();
        });

    }

    private boolean installMagisk(String fileName, String moduleName) {
        String base = getMContext().getFilesDir().getAbsolutePath();
        FileHelper.saveAssets(getMContext(), fileName, base);
        FileHelper.saveAssets(getMContext(), "installer", base);
        List<String> commands = new ArrayList<>();
        commands.add("chmod 777 " + base + "/installer");
        commands.add("sh " + base + "/installer dummy 1 " + base + "/" + fileName);
        List<String> result = Shell.SU.run(commands);

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
        textView.setText(Html.fromHtml(result.stream().reduce(moduleName + "<br>" , (a, b) -> a + "<br>" + b)+"<br><br>" +(resultB ? "<font color='#dd2c00'><storage>!!安装成功,重启生效!!</b></font><br>" : "<font color='#dd2c00'><b>!!安装失败!!</b></font><br>")));

        return resultB;
    }

    private void installByCopy(String fileName) {
        String base = getMContext().getFilesDir().getAbsolutePath();
        Shell.SU.run("rm -rf " + base + "/data");
        Shell.SU.run("rm -rf " + base + "/system");

        FileHelper.UnZipAssetsFolder(getMContext(), fileName, base);
        Shell.SU.run("mount -o rw,remount /system");
        Shell.SU.run("mv -f " + base + "/data /");
        Shell.SU.run("mv " + base + "/system /");
    }
}