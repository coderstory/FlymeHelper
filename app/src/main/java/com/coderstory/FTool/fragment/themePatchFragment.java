package com.coderstory.FTool.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.Switch;

import com.coderstory.FTool.R;
import com.coderstory.FTool.utils.dialog.SweetAlertDialog;

import java.util.ArrayList;

import ren.solid.library.fragment.base.BaseFragment;
import ren.solid.library.utils.SnackBarUtils;

import static com.coderstory.FTool.utils.root.ShellUtils.execute;
import static com.coderstory.FTool.utils.root.SuHelper.canRunRootCommands;


public class themePatchFragment extends BaseFragment {
    private Handler handler = new Handler();
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_themepatch;
    }


    @Override
    protected void setUpView() {


            $(R.id.enableThemePatch).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canRunRootCommands()) {
                        getEditor().putBoolean("enableThemePatch", ((Switch) v).isChecked());
                        getEditor().apply();

                        final SweetAlertDialog dialog = new SweetAlertDialog(getMContext());
                        dialog.setTitleText("正在处理,请稍候...");
                        dialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                            }
                        }, 2000);

                        if (((Switch) v).isChecked()) {
                            new Thread() {
                                @Override
                                public void run() {
                                    ArrayList<String> list = new ArrayList<>();
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.helper.BootBroadcastReceiver");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.font.FontTrialService");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.theme.ThemeTrialService");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.service.ThemeRestoreService");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.service.FontRestoreService");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.gslb.push.GslbDataRefreshReceiver");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.push.CustomizePushReceiver");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.helper.ShopDemoReceiver");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.cloud.pushsdk.SystemReceiver");
                                    list.add("pm disable com.meizu.customizecenter/com.meizu.advertise.api.AppDownloadAndInstallReceiver");
                                    execute(list);
                                }
                            }.start();
                        }
                    } else {
                        //  T.showAnimErrorToast(this.getMContext(), "尚未获取Root权限");
                        SnackBarUtils.makeLong($(R.id.enableThemePatch), "尚未获取Root权限！").show();
                    }
                }
            });

        $(R.id.enableCheckInstaller).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditor().putBoolean("enableCheckInstaller", ((Switch) v).isChecked());
                getEditor().apply();
            }
        });
    }
    @Override
    protected void setUpData() {

        ((Switch) $(R.id.enableThemePatch)).setChecked(getPrefs().getBoolean("enableThemePatch", false));

        ((Switch) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
    }






}
