package com.coderstory.FTool.fragment;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.view.View;
import android.widget.Switch;
import com.coderstory.FTool.R;
import com.coderstory.FTool.utils.app.checkAppVersion;
import com.coderstory.FTool.utils.dialog.SweetAlertDialog;
import java.util.ArrayList;
import java.util.List;

import ren.solid.library.fragment.base.BaseFragment;
import ren.solid.library.utils.SnackBarUtils;
import static com.coderstory.FTool.utils.root.ShellUtils.execute;
import static com.coderstory.FTool.utils.root.SuHelper.canRunRootCommands;

public class themePatchFragment extends BaseFragment {
    private Handler handler = new Handler();

    List<String> needDisableStr =new ArrayList<>();
    String packageName="pm disable com.meizu.customizecenter/";

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_themepatch;
    }


    @Override
    protected void setUpView() {

        needDisableStr.add("com.meizu.customizecenter.common.helper.BootBroadcastReceiver");
        needDisableStr.add("com.meizu.customizecenter.common.font.FontTrialService");
        needDisableStr.add("com.meizu.customizecenter.common.theme.ThemeTrialService");
        needDisableStr.add("com.meizu.customizecenter.service.ThemeRestoreService");
        needDisableStr.add("com.meizu.customizecenter.service.FontRestoreService");
        needDisableStr.add("com.meizu.gslb.push.GslbDataRefreshReceiver");
        needDisableStr.add("com.meizu.customizecenter.common.push.CustomizePushReceiver");
        needDisableStr.add("com.meizu.customizecenter.common.helper.ShopDemoReceiver");
        needDisableStr.add("com.meizu.cloud.pushsdk.SystemReceiver");
        needDisableStr.add("com.meizu.advertise.api.AppDownloadAndInstallReceiver");

        if(  !   new checkAppVersion().isSupport(getMContext())){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.Tips_Title);
            dialog.setMessage(R.string.notSupportVersionTips);
            dialog.setPositiveButton(R.string.Btn_Sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }

            $(R.id.enableThemePatch).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canRunRootCommands()) {
                        getEditor().putBoolean("enableThemePatch", ((Switch) v).isChecked());
                        getEditor().apply();

                        final SweetAlertDialog dialog = new SweetAlertDialog(getMContext());
                        dialog.setTitleText(getString(R.string.processing));
                        dialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                            }
                        }, 2000);

                        if (((Switch) v).isChecked()) {
                            disableApplication();
                        }
                    } else {
                        //  T.showAnimErrorToast(this.getMContext(), "尚未获取Root权限");
                        SnackBarUtils.makeLong($(R.id.enableThemePatch), getString(R.string.noRootTips)).show();
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

        //检测被禁用的组建是否被恢复 目前仅判断 BOOT COMPLETED
        if (getPrefs().getBoolean("enableThemePatch", false)) {
            PackageManager pm = getActivity().getPackageManager();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_BOOT_COMPLETED);
            List<ResolveInfo> resolveInfoList = pm.queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);//MATCH_DISABLED_COMPONENTS
            for (ResolveInfo resolveInfo : resolveInfoList) {
                if (resolveInfo.activityInfo.applicationInfo.packageName.equals("com.meizu.customizecenter")) {
                    String name = resolveInfo.activityInfo.name;
                    for (String str : needDisableStr) {
                        if (str.equals(name)) {
                            ComponentName mComponentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                            // pm.setComponentEnabledSetting(mComponentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                            disableApplication();
                            break;
                        }
                    }
                }
            }
        }
    }
    @Override
    protected void setUpData() {
        ((Switch) $(R.id.enableThemePatch)).setChecked(getPrefs().getBoolean("enableThemePatch", false));
        ((Switch) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
    }

    void  disableApplication(){
        new Thread() {
            @Override
            public void run() {
                ArrayList<String> list = new ArrayList<>();

                for (String str: needDisableStr) {
                    list.add(packageName+str);
                }

                execute(list);
            }
        }.start();
    }
}
