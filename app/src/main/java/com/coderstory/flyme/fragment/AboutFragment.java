package com.coderstory.flyme.fragment;

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.coderstory.flyme.BuildConfig;
import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.licensesdialog.LicensesDialog;
import com.coderstory.flyme.utils.licensesdialog.licenses.ApacheSoftwareLicense20;
import com.coderstory.flyme.utils.licensesdialog.licenses.GnuGeneralPublicLicense20;
import com.coderstory.flyme.utils.licensesdialog.model.Notice;
import com.coderstory.flyme.utils.licensesdialog.model.Notices;

import static com.coderstory.flyme.utils.Utils.vi;
import static com.coderstory.flyme.utils.Utils.vp;


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

        if (vi()) {
            ((TextView) $(R.id.mark)).setText("有效期剩余:" + vp() + "天");
        } else {
            ((TextView) $(R.id.mark)).setText("当前版本已过期");
        }
    }
}
