package com.coderstory.flyme.fragment

import android.view.View
import android.widget.TextView
import com.coderstory.flyme.BuildConfig
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment
import com.coderstory.flyme.tools.licensesdialog.LicensesDialog
import com.coderstory.flyme.tools.licensesdialog.licenses.ApacheSoftwareLicense20
import com.coderstory.flyme.tools.licensesdialog.licenses.GnuGeneralPublicLicense20
import com.coderstory.flyme.tools.licensesdialog.model.Notice
import com.coderstory.flyme.tools.licensesdialog.model.Notices

class AboutFragment : BaseFragment() {
    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_about
    }

    override fun setUpView() {
        (`$`<View>(R.id.version) as TextView).text = BuildConfig.VERSION_NAME
        `$`<View>(R.id.os).setOnClickListener { v: View? ->
            val notices = Notices()
            notices.addNotice(Notice("ApacheSoftwareLicense", "", "", ApacheSoftwareLicense20()))
            notices.addNotice(Notice("GnuGeneralPublicLicense", "", "", GnuGeneralPublicLicense20()))
            LicensesDialog.Builder(mContext)
                    .setNotices(notices)
                    .build()
                    .show()
        }
    }
}