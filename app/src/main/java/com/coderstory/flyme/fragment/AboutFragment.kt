package com.coderstory.flyme.fragment;

import android.widget.TextView;

import com.coderstory.flyme.BuildConfig;
import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.tools.licensesdialog.LicensesDialog;
import com.coderstory.flyme.tools.licensesdialog.licenses.ApacheSoftwareLicense20;
import com.coderstory.flyme.tools.licensesdialog.licenses.GnuGeneralPublicLicense20;
import com.coderstory.flyme.tools.licensesdialog.model.Notice;
import com.coderstory.flyme.tools.licensesdialog.model.Notices;

public class AboutFragment extends BaseFragment {
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_about;
    }

    @Override
    protected void setUpView() {
        ((TextView) $(R.id.version)).setText(BuildConfig.VERSION_NAME);
        $(R.id.os).setOnClickListener(v -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("ApacheSoftwareLicense", "", "", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("GnuGeneralPublicLicense", "", "", new GnuGeneralPublicLicense20()));

            new LicensesDialog.Builder(getMContext())
                    .setNotices(notices)
                    .build()
                    .show();
        });
    }
}
