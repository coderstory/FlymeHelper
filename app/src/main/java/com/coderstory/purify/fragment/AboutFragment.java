package com.coderstory.purify.fragment;

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.coderstory.flyme.BuildConfig;
import com.coderstory.flyme.R;
import com.coderstory.purify.fragment.base.BaseFragment;
import com.coderstory.purify.utils.licensesdialog.LicensesDialog;
import com.coderstory.purify.utils.licensesdialog.licenses.ApacheSoftwareLicense20;
import com.coderstory.purify.utils.licensesdialog.licenses.GnuGeneralPublicLicense20;
import com.coderstory.purify.utils.licensesdialog.model.Notice;
import com.coderstory.purify.utils.licensesdialog.model.Notices;


public class AboutFragment extends BaseFragment {


    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_about;
    }

    @Override
    protected void setUpView() {
        TextView tv_content = $(R.id.tv_content);
        tv_content.setAutoLinkMask(Linkify.ALL);
        tv_content.setMovementMethod(LinkMovementMethod
                .getInstance());

        $(R.id.os).setOnClickListener(v -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("ApacheSoftwareLicense", "", "", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("GnuGeneralPublicLicense", "", "", new GnuGeneralPublicLicense20()));

            new LicensesDialog.Builder(getMContext())
                    .setNotices(notices)
                    .build()
                    .show();
        });

        ((TextView) $(R.id.version)).setText(BuildConfig.VERSION_NAME);
    }
}
