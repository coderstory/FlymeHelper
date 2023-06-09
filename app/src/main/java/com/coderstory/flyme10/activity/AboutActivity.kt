package com.coderstory.flyme10.activity

import androidx.fragment.app.Fragment
import com.coderstory.flyme10.fragment.AboutFragment

open class AboutActivity : ToolbarActivity() {
    override fun setFragment(): Fragment {
        return AboutFragment()
    }

    override val toolbarTitle: String
        get() = "关于"
}