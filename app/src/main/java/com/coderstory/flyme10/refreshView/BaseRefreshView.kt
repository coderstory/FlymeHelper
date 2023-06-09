package com.coderstory.flyme10.refreshView

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import com.coderstory.flyme10.view.PullToRefreshView

abstract class BaseRefreshView internal constructor(private val mRefreshLayout: PullToRefreshView?) :
    Drawable(), Drawable.Callback, Animatable {
    val context: Context
        get() = mRefreshLayout!!.context

    abstract fun setPercent(percent: Float, invalidate: Boolean)
    abstract fun offsetTopAndBottom(offset: Int)
    override fun invalidateDrawable(who: Drawable) {
        val callback = callback
        callback?.invalidateDrawable(this)
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        val callback = callback
        callback?.scheduleDrawable(this, what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        val callback = callback
        callback?.unscheduleDrawable(this, what)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}
}