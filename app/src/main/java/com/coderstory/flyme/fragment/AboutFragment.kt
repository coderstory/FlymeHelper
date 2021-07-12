package com.coderstory.flyme.fragment

import android.view.View
import android.widget.TextView
import com.coderstory.flyme.BuildConfig
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment

class AboutFragment : BaseFragment() {
    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_about
    }

    override fun setUpView() {
        (`$`<View>(R.id.version) as TextView).text = BuildConfig.VERSION_NAME
    }
}