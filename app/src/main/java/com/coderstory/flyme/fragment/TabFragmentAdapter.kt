package com.coderstory.flyme.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabFragmentAdapter internal constructor(fm: FragmentManager?, private val fragments: List<Fragment>, private val args: Bundle) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        val fragment = fragments[position]
        fragment.arguments = args
        return fragment
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

}