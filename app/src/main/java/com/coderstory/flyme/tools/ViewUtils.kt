package com.coderstory.flyme.tools

import com.coderstory.flyme.fragment.base.BaseFragment
import java.util.*

object ViewUtils {
    private val fragmentList: MutableMap<String, BaseFragment?> = HashMap()

    /**
     * 根据Class创建Fragment
     *
     * @param clazz the Fragment of create
     * @return
     */
    private fun createFragment(clazz: Class<*>, isObtain: Boolean): BaseFragment? {
        var resultFragment: BaseFragment? = null
        val className = clazz.name
        if (fragmentList.containsKey(className)) {
            resultFragment = fragmentList[className]
        } else {
            try {
                try {
                    resultFragment = Class.forName(className).newInstance() as BaseFragment
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            if (isObtain) fragmentList[className] = resultFragment
        }
        return resultFragment
    }

    fun createFragment(clazz: Class<*>): BaseFragment? {
        return createFragment(clazz, true)
    }
}