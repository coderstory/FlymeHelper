package com.coderstory.FTool.fragment;

import android.app.AlertDialog;
import android.widget.Switch;

import com.coderstory.FTool.R;
import com.coderstory.FTool.utils.app.checkAppVersion;

import ren.solid.library.fragment.base.BaseFragment;
import ren.solid.library.utils.SnackBarUtils;

import static com.coderstory.FTool.utils.root.SuHelper.canRunRootCommands;

public class themePatchFragment extends BaseFragment {
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_themepatch;
    }


    @Override
    protected void setUpView() {

        if (!new checkAppVersion().isSupport(getMContext())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.Tips_Title);
            dialog.setMessage(R.string.notSupportVersionTips);
            dialog.setPositiveButton(R.string.Btn_Sure, (dialog1, which) -> dialog1.cancel());

            dialog.show();
        }

        $(R.id.enableThemePatch).setOnClickListener(v -> {
            if (canRunRootCommands()) {
                getEditor().putBoolean("enableThemePatch", ((Switch) v).isChecked());
                getEditor().apply();
                sudoFixPermissions();
            } else {
                SnackBarUtils.makeLong($(R.id.enableThemePatch), getString(R.string.noRootTips)).show();
            }
        });

        $(R.id.enableCTS).setOnClickListener(v -> {
            getEditor().putBoolean("enableCTS", ((Switch) v).isChecked());
            getEditor().apply();
            sudoFixPermissions();
        });
    }

    @Override
    protected void setUpData() {
        ((Switch) $(R.id.enableThemePatch)).setChecked(getPrefs().getBoolean("enableThemePatch", false));
        ((Switch) $(R.id.enableCheckInstaller)).setChecked(getPrefs().getBoolean("enableCheckInstaller", false));
        ((Switch) $(R.id.enableCTS)).setChecked(getPrefs().getBoolean("enableCTS", false));
    }
}
