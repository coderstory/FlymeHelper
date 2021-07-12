package com.coderstory.flyme.activity

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.coderstory.flyme.R
import com.coderstory.flyme.R.id
import com.coderstory.flyme.activity.base.BaseActivity
import com.google.android.material.appbar.AppBarLayout

abstract class ToolbarActivity : BaseActivity() {
    protected var mToolbar: Toolbar? = null
    protected var mAppBarLayout: AppBarLayout? = null
    protected var mFragmentManager: FragmentManager? = null
    override fun init() {
        mFragmentManager = supportFragmentManager
    }

    override fun setUpView() {
        mAppBarLayout = `$`(id.appbar_layout)
        mToolbar = `$`(id.toolbar)
        mToolbar!!.title = toolbarTitle
        setUpToolBar()
    }

    private fun setUpToolBar() {
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationIcon(R.drawable.ic_back)
        mToolbar!!.setNavigationOnClickListener { view: View? -> onBackPressed() }
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
    }

    protected abstract val toolbarTitle: String
    override fun setUpData() {
        mFragmentManager!!.beginTransaction().replace(id.fl_content, setFragment()).commit()
    }

    protected abstract fun setFragment(): Fragment
    override fun setLayoutResourceID(): Int {
        return R.layout.activity_content
    }
}