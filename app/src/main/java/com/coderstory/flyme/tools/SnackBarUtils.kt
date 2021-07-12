package com.coderstory.flyme.tools

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackBarUtils private constructor(private val mSnackbar: Snackbar) {
    private fun getSnackBarLayout(snackbar: Snackbar?): View? {
        return snackbar?.view
    }

    private fun setSnackBarBackColor(colorId: Int): Snackbar {
        val snackBarView = getSnackBarLayout(mSnackbar)
        snackBarView?.setBackgroundColor(colorId)
        return mSnackbar
    }

    fun info() {
        setSnackBarBackColor(color_info)
        show()
    }

    fun info(actionText: String?, listener: View.OnClickListener?) {
        setSnackBarBackColor(color_info)
        show(actionText, listener)
    }

    fun warning() {
        setSnackBarBackColor(color_warning)
        show()
    }

    fun warning(actionText: String?, listener: View.OnClickListener?) {
        setSnackBarBackColor(color_warning)
        show(actionText, listener)
    }

    fun danger() {
        setSnackBarBackColor(color_danger)
        show()
    }

    fun danger(actionText: String?, listener: View.OnClickListener?) {
        setSnackBarBackColor(color_danger)
        show(actionText, listener)
    }

    fun success() {
        setSnackBarBackColor(color_success)
        show()
    }

    fun success(actionText: String?, listener: View.OnClickListener?) {
        setSnackBarBackColor(color_success)
        show(actionText, listener)
    }

    fun show() {
        mSnackbar.show()
    }

    fun show(actionText: String?, listener: View.OnClickListener?) {
        mSnackbar.setActionTextColor(color_action)
        mSnackbar.setAction(actionText, listener).show()
    }

    companion object {
        private const val color_danger = -0x56bbbe
        private const val color_success = -0xc389c3
        private const val color_info = -0xd6490a
        private const val color_warning = -0x7592c5
        private const val color_action = -0x323a41
        fun makeShort(view: View?, text: String?): SnackBarUtils {
            val snackbar = Snackbar.make(view!!, text!!, Snackbar.LENGTH_SHORT)
            return SnackBarUtils(snackbar)
        }

        fun makeLong(view: View?, text: String?): SnackBarUtils {
            val snackbar = Snackbar.make(view!!, text!!, Snackbar.LENGTH_LONG)
            return SnackBarUtils(snackbar)
        }
    }
}