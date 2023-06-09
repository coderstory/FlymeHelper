package com.coderstory.flyme10.tools

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackBarUtils private constructor(private val mSnack: Snackbar) {
    private fun getSnackBarLayout(snackbar: Snackbar?): View? {
        return snackbar?.view
    }

    private fun setSnackBarBackColor(colorId: Int): Snackbar {
        val snackBarView = getSnackBarLayout(mSnack)
        snackBarView?.setBackgroundColor(colorId)
        return mSnack
    }

    fun info() {
        setSnackBarBackColor(color_info)
        show()
    }

    fun danger() {
        setSnackBarBackColor(color_danger)
        show()
    }

    fun show() {
        mSnack.show()
    }

    fun show(actionText: String?, listener: View.OnClickListener?) {
        mSnack.setActionTextColor(color_action)
        mSnack.setAction(actionText, listener).show()
    }

    companion object {
        private const val color_danger = -0x56bbbe
        private const val color_info = -0xd6490a
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