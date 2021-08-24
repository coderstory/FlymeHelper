package com.coderstory.flyme.refreshView

import android.graphics.*
import android.graphics.drawable.Animatable
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.coderstory.flyme.R
import com.coderstory.flyme.refreshView.SunRefreshView
import com.coderstory.flyme.tools.Utils
import com.coderstory.flyme.view.PullToRefreshView

/**
 * Created by Oleksii Shliama on 22/12/2014.
 * https://dribbble.com/shots/1650317-Pull-to-Refresh-Rentals
 */
class SunRefreshView(private val mParent: PullToRefreshView) : BaseRefreshView(mParent), Animatable {
    private val mMatrix: Matrix
    private val mSunSize = 100
    private var mAnimation: Animation? = null
    private var mTop = 0
    private var mScreenWidth = 0
    private var mSkyHeight = 0
    private var mSkyTopOffset = 0f
    private var mSkyMoveOffset = 0f
    private var mTownHeight = 0
    private var mTownInitialTopOffset = 0f
    private var mTownFinalTopOffset = 0f
    private var mTownMoveOffset = 0f
    private var mSunLeftOffset = 0f
    private var mSunTopOffset = 0f
    private var mPercent = 0.0f
    private var mRotate = 0.0f
    private var mSky: Bitmap? = null
    private var mSun: Bitmap? = null
    private var mTown: Bitmap? = null
    private var isRefreshing = false
    private fun initiateDimens(viewWidth: Int) {
        if (viewWidth <= 0 || viewWidth == mScreenWidth) return
        mScreenWidth = viewWidth
        mSkyHeight = (SKY_RATIO * mScreenWidth).toInt()
        mSkyTopOffset = mSkyHeight * 0.38f
        mSkyMoveOffset = Utils.convertDpToPixel(context, 15).toFloat()
        mTownHeight = (TOWN_RATIO * mScreenWidth).toInt()
        mTownInitialTopOffset = mParent.totalDragDistance - mTownHeight * SunRefreshView.Companion.TOWN_INITIAL_SCALE
        mTownFinalTopOffset = mParent.totalDragDistance - mTownHeight * SunRefreshView.Companion.TOWN_FINAL_SCALE
        mTownMoveOffset = Utils.convertDpToPixel(context, 10).toFloat()
        mSunLeftOffset = 0.3f * mScreenWidth.toFloat()
        mSunTopOffset = mParent.totalDragDistance * 0.1f
        mTop = -mParent.totalDragDistance
        createBitmaps()
    }

    private fun createBitmaps() {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        mSky = BitmapFactory.decodeResource(context.resources, R.drawable.sky, options)
        mSky = Bitmap.createScaledBitmap(mSky!!, mScreenWidth, mSkyHeight, true)
        mTown = BitmapFactory.decodeResource(context.resources, R.drawable.buildings, options)
        mTown = Bitmap.createScaledBitmap(mTown!!, mScreenWidth, (mScreenWidth * SunRefreshView.Companion.TOWN_RATIO).toInt(), true)
        mSun = BitmapFactory.decodeResource(context.resources, R.drawable.sun, options)
        mSun = Bitmap.createScaledBitmap(mSun!!, mSunSize, mSunSize, true)
    }

    override fun setPercent(percent: Float, invalidate: Boolean) {
        setPercent(percent)
        if (invalidate) setRotate(percent)
    }

    override fun offsetTopAndBottom(offset: Int) {
        mTop += offset
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (mScreenWidth <= 0) return
        val saveCount = canvas.save()
        canvas.translate(0f, mTop.toFloat())
        canvas.clipRect(0, -mTop, mScreenWidth, mParent.totalDragDistance)
        drawSky(canvas)
        drawSun(canvas)
        drawTown(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun drawSky(canvas: Canvas) {
        val matrix = mMatrix
        matrix.reset()
        val dragPercent = Math.min(1f, Math.abs(mPercent))
        val skyScale: Float
        val scalePercentDelta: Float = dragPercent - SunRefreshView.Companion.SCALE_START_PERCENT
        skyScale = if (scalePercentDelta > 0) {
            val scalePercent: Float = scalePercentDelta / (1.0f - SunRefreshView.Companion.SCALE_START_PERCENT)
            SunRefreshView.Companion.SKY_INITIAL_SCALE - (SunRefreshView.Companion.SKY_INITIAL_SCALE - 1.0f) * scalePercent
        } else {
            SunRefreshView.Companion.SKY_INITIAL_SCALE
        }
        val offsetX = -(mScreenWidth * skyScale - mScreenWidth) / 2.0f
        val offsetY = (((1.0f - dragPercent) * mParent.totalDragDistance - mSkyTopOffset // Offset canvas moving
                - mSkyHeight * (skyScale - 1.0f) / 2) // Offset sky scaling
                + mSkyMoveOffset * dragPercent) // Give it a little move top -> bottom
        matrix.postScale(skyScale, skyScale)
        matrix.postTranslate(offsetX, offsetY)
        canvas.drawBitmap(mSky!!, matrix, null)
    }

    private fun drawTown(canvas: Canvas) {
        val matrix = mMatrix
        matrix.reset()
        val dragPercent = Math.min(1f, Math.abs(mPercent))
        val townScale: Float
        val townTopOffset: Float
        val townMoveOffset: Float
        val scalePercentDelta: Float = dragPercent - SunRefreshView.Companion.SCALE_START_PERCENT
        if (scalePercentDelta > 0) {
            val scalePercent: Float = scalePercentDelta / (1.0f - SunRefreshView.Companion.SCALE_START_PERCENT)
            townScale = SunRefreshView.Companion.TOWN_INITIAL_SCALE + (SunRefreshView.Companion.TOWN_FINAL_SCALE - SunRefreshView.Companion.TOWN_INITIAL_SCALE) * scalePercent
            townTopOffset = mTownInitialTopOffset - (mTownFinalTopOffset - mTownInitialTopOffset) * scalePercent
            townMoveOffset = mTownMoveOffset * (1.0f - scalePercent)
        } else {
            val scalePercent: Float = dragPercent / SunRefreshView.Companion.SCALE_START_PERCENT
            townScale = SunRefreshView.Companion.TOWN_INITIAL_SCALE
            townTopOffset = mTownInitialTopOffset
            townMoveOffset = mTownMoveOffset * scalePercent
        }
        val offsetX = -(mScreenWidth * townScale - mScreenWidth) / 2.0f
        val offsetY = ((1.0f - dragPercent) * mParent.totalDragDistance // Offset canvas moving
                + townTopOffset
                - mTownHeight * (townScale - 1.0f) / 2 // Offset town scaling
                + townMoveOffset) // Give it a little move
        matrix.postScale(townScale, townScale)
        matrix.postTranslate(offsetX, offsetY)
        canvas.drawBitmap(mTown!!, matrix, null)
    }

    private fun drawSun(canvas: Canvas) {
        val matrix = mMatrix
        matrix.reset()
        var dragPercent = mPercent
        if (dragPercent > 1.0f) { // Slow down if pulling over set height
            dragPercent = (dragPercent + 9.0f) / 10
        }
        val sunRadius = mSunSize.toFloat() / 2.0f
        var sunRotateGrowth: Float = SunRefreshView.Companion.SUN_INITIAL_ROTATE_GROWTH
        var offsetX = mSunLeftOffset
        var offsetY = (mSunTopOffset
                + mParent.totalDragDistance / 2 * (1.0f - dragPercent) // Move the sun up
                - mTop) // Depending on Canvas position
        val scalePercentDelta: Float = dragPercent - SunRefreshView.Companion.SCALE_START_PERCENT
        if (scalePercentDelta > 0) {
            val scalePercent: Float = scalePercentDelta / (1.0f - SunRefreshView.Companion.SCALE_START_PERCENT)
            val sunScale: Float = 1.0f - (1.0f - SunRefreshView.Companion.SUN_FINAL_SCALE) * scalePercent
            sunRotateGrowth += (SunRefreshView.Companion.SUN_FINAL_ROTATE_GROWTH - SunRefreshView.Companion.SUN_INITIAL_ROTATE_GROWTH) * scalePercent
            matrix.preTranslate(offsetX + (sunRadius - sunRadius * sunScale), offsetY * (2.0f - sunScale))
            matrix.preScale(sunScale, sunScale)
            offsetX += sunRadius
            offsetY = offsetY * (2.0f - sunScale) + sunRadius * sunScale
        } else {
            matrix.postTranslate(offsetX, offsetY)
            offsetX += sunRadius
            offsetY += sunRadius
        }
        matrix.postRotate(
                (if (isRefreshing) -360 else 360) * mRotate * (if (isRefreshing) 1F else sunRotateGrowth),
                offsetX,
                offsetY)
        canvas.drawBitmap(mSun!!, matrix, null)
    }

    private fun setPercent(percent: Float) {
        mPercent = percent
    }

    private fun setRotate(rotate: Float) {
        mRotate = rotate
        invalidateSelf()
    }

    private fun resetOriginals() {
        setPercent(0f)
        setRotate(0f)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, mSkyHeight + top)
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun start() {
        mAnimation!!.reset()
        isRefreshing = true
        mParent.startAnimation(mAnimation)
    }

    override fun stop() {
        mParent.clearAnimation()
        isRefreshing = false
        resetOriginals()
    }

    private fun setupAnimations() {
        val animation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setRotate(interpolatedTime)
            }
        }
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART
        animation.interpolator = LINEAR_INTERPOLATOR
        animation.duration = ANIMATION_DURATION.toLong()
        mAnimation = animation
    }

    companion object {
        private const val SCALE_START_PERCENT = 0.5f
        private const val ANIMATION_DURATION = 1000
        private const val SKY_RATIO = 0.65f
        private const val SKY_INITIAL_SCALE = 1.05f
        private const val TOWN_RATIO = 0.22f
        private const val TOWN_INITIAL_SCALE = 1.20f
        private const val TOWN_FINAL_SCALE = 1.30f
        private const val SUN_FINAL_SCALE = 0.75f
        private const val SUN_INITIAL_ROTATE_GROWTH = 1.2f
        private const val SUN_FINAL_ROTATE_GROWTH = 1.5f
        private val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
    }

    init {
        mMatrix = Matrix()
        setupAnimations()
        mParent.post { initiateDimens(mParent.width) }
    }
}