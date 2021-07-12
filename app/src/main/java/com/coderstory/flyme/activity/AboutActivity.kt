package com.coderstory.flyme.activity

import androidx.fragment.app.Fragment
import com.coderstory.flyme.fragment.AboutFragment

open class AboutActivity : ToolbarActivity() {
    override fun setFragment(): Fragment {
        return AboutFragment()
    }

    override val toolbarTitle: String
        get() = "关于"
}