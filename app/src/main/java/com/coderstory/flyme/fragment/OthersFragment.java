package com.coderstory.flyme.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.AppSignCheck;
import com.coderstory.flyme.utils.Misc;
import com.coderstory.flyme.utils.SharedHelper;
import com.coderstory.flyme.utils.Utils;
import com.topjohnwu.superuser.Shell;

import java.lang.reflect.Field;
import java.util.List;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;


public class OthersFragment extends BaseFragment {
    ProgressDialog dialog;

    public OthersFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_upgrade_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Layer anyLayer = AnyLayer.dialog(getMContext())
                .contentView(R.layout.dialog_tdisable_app)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick((AnyLayer, v) -> AnyLayer.dismiss(), R.id.fl_dialog_no)
                .onClick((AnyLayer, v) -> {
                    Shell.su("killall com.android.systemui").exec();
                    Shell.su("am force-stop com.meizu.flyme.launcher").exec();
                    System.exit(0);

                }, R.id.fl_dialog_yes);
        anyLayer.show();

        CardView cardView = (CardView) ((DialogLayer) anyLayer).getContentView();
        LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
        TextView textView = (TextView) linearLayout.getChildAt(1);
        textView.setText("一键重启桌面状态栏包管理器等app");
        return false;
    }

    @Override
    protected void setUpView() {
        setDatePickerDividerColor($(R.id.home_icon_num_column), 7, 4);
        setDatePickerDividerColor($(R.id.home_icon_num_rows), 7, 4);
        setDatePickerDividerColor($(R.id.home_icon_num_hot_seat_icons), 5, 1);

        $(R.id.enableBlockAD).setOnClickListener(v -> {
            getEditor().putBoolean("EnableBlockAD", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
            if (((androidx.appcompat.widget.SwitchCompat) v).isChecked()) {
                dialog = ProgressDialog.show(getMContext(), "分析应用中...", "", true, false, null);
                new Thread(() -> {
                    List<String> paths = Shell.su("cd /data/data;find -name com.meizu.advertise.plugin   -type dir").exec().getOut();
                    String[] command = new String[paths.size()];
                    if (paths.size() == 0) {
                        ((Activity) getMContext()).runOnUiThread(() -> {
                            dialog.setMessage("处理失败,请重试");
                            getEditor().putBoolean("EnableBlockAD", false);
                            ((androidx.appcompat.widget.SwitchCompat) $(R.id.enableBlockAD)).isChecked();
                        });
                        return;
                    }
                    for (int i = 0; i < paths.size(); i++) {
                        String path = paths.get(i).substring(1);
                        ((Activity) getMContext()).runOnUiThread(() -> dialog.setMessage("正在处理\n" + path.split("/file")[0].replace("/", "")));
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        command[i] = "rm -rf  /data/data" + path + "/*" + ";" + "chmod 0000 " + "/data/data" + path;
                        Shell.su(command[i]).exec();
                    }
                    ((Activity) getMContext()).runOnUiThread(() -> dialog.dismiss());
                }).start();
            }
        });


        $(R.id.enabletheme).setOnClickListener(v -> {
            getEditor().putBoolean("enabletheme", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());

            AppSignCheck a = new AppSignCheck(getMContext(), Misc.key);
            if (!a.check()) {
                getEditor().putString("isCore", "1");
            }
            fix();
        });
        $(R.id.HideRootWithPay).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootWithPay", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.HideRootWithUpgrade).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootWithUpgrade", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_label).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_label", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_45).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_4", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_5).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_5", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.hide_icon_6).setOnClickListener(v -> {
            getEditor().putBoolean("hide_icon_6", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.enableCTS).setOnClickListener(v -> {
            getEditor().putBoolean("enableCTS", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.enableCheckInstaller).setOnClickListener(v -> {
            getEditor().putBoolean("enableCheckInstaller", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.hideDepWarn).setOnClickListener(v -> {
            getEditor().putBoolean("hideDepWarn", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });


        $(R.id.removeStore).setOnClickListener(v -> {
            getEditor().putBoolean("removeStore", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        $(R.id.autoInstall).setOnClickListener(v -> {
            getEditor().putBoolean("autoInstall", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.HideRootGlobal).setOnClickListener(v -> {
            getEditor().putBoolean("HideRootGlobal", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });

        ((NumberPicker) $(R.id.home_icon_num_column)).setOnValueChangedListener((v, oldValue, newValue) -> {
            getEditor().putInt("home_icon_num_column", newValue);
            fix();
        });
        ((NumberPicker) $(R.id.home_icon_num_rows)).setOnValueChangedListener((v, oldValue, newValue) -> {
            getEditor().putInt("home_icon_num_rows", newValue);
            fix();
        });

        $(R.id.disableSearch).setOnClickListener(v -> {
            getEditor().putBoolean("disableSearch", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
        $(R.id.mms).setOnClickListener(v -> {
            getEditor().putBoolean("mms", ((androidx.appcompat.widget.SwitchCompat) v).isChecked());
            fix();
        });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_others;
    }

    @Override
    protected void setUpData() {
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_label)).setChecked(getPrefs().getBoolean("hide_icon_label", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.enableBlockAD)).setChecked(getPrefs().getBoolean("EnableBlockAD", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.enabletheme)).setChecked(getPrefs().getBoolean("enabletheme", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.HideRootWithPay)).setChecked(getPrefs().getBoolean("HideRootWithPay", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.HideRootWithUpgrade)).setChecked(getPrefs().getBoolean("HideRootWithUpgrade", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_45)).setChecked(getPrefs().getBoolean("hide_icon_4", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_5)).setChecked(getPrefs().getBoolean("hide_icon_5", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hide_icon_6)).setChecked(getPrefs().getBoolean("hide_icon_6", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.enableCTS)).setChecked(getPrefs().getBoolean("enableCTS", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.hideDepWarn)).setChecked(getPrefs().getBoolean("hideDepWarn", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.removeStore)).setChecked(getPrefs().getBoolean("removeStore", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.autoInstall)).setChecked(getPrefs().getBoolean("autoInstall", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.HideRootGlobal)).setChecked(getPrefs().getBoolean("HideRootGlobal", false));
        ((NumberPicker) $(R.id.home_icon_num_column)).setValue(getPrefs().getInt("home_icon_num_column", 4));
        ((NumberPicker) $(R.id.home_icon_num_rows)).setValue(getPrefs().getInt("home_icon_num_rows", 5));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.disableSearch)).setChecked(getPrefs().getBoolean("disableSearch", false));
        ((androidx.appcompat.widget.SwitchCompat) $(R.id.mms)).setChecked(getPrefs().getBoolean("mms", false));

        if (!Utils.check(new SharedHelper(getMContext()))) {
            $(R.id.removeStore).setEnabled(false);
            $(R.id.autoInstall).setEnabled(false);
            $(R.id.home_icon_num_column).setEnabled(false);
            $(R.id.home_icon_num_rows).setEnabled(false);

            ((TextView) $(R.id.test1)).setTextColor(Color.parseColor("#A9A9A9"));
            ((TextView) $(R.id.test2)).setTextColor(Color.parseColor("#A9A9A9"));
        }
    }

    private void setDatePickerDividerColor(NumberPicker picker, int max, int min) {
        //设置最大值
        picker.setMaxValue(max);
        //设置最小值
        picker.setMinValue(min);
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, new ColorDrawable(Color.alpha(256)));
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        setUpData();
    }
}
