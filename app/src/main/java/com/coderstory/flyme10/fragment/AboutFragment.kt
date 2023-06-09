package com.coderstory.flyme10.fragment

import android.view.View
import android.widget.TextView
import com.coderstory.flyme10.BuildConfig

import com.coderstory.flyme10.R
import com.coderstory.flyme10.fragment.base.BaseFragment

class AboutFragment : BaseFragment() {
    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_about
    }

    override fun setUpView() {
        (`$`<View>(R.id.version) as TextView).text = BuildConfig.VERSION_NAME
    }
}