package com.coderstory.flyme.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.ImageView
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import com.coderstory.flyme.R
import com.coderstory.flyme.refreshView.BaseRefreshView
import com.coderstory.flyme.refreshView.SunRefreshView
import com.coderstory.flyme.tools.Utils
import java.security.InvalidParameterException

class PullToRefreshView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {
    private val mRefreshView: ImageView
    private val mDecelerateInterpolator: Interpolator
    private val mTouchSlop: Int
    var totalDragDistance: Int = 0
    private var mTarget: View? = null
    private var mBaseRefreshView: BaseRefreshView? = null
    private var mCurrentDragPercent = 0f
    private var mCurrentOffsetTop = 0
    private val mToStartListener: Animation.AnimationListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            mBaseRefreshView!!.stop()
            mCurrentOffsetTop = mTarget!!.top
        }
    }
    private var mRefreshing = false
    private var mActivePointerId = 0
    private var mIsBeingDragged = false
    private var mInitialMotionY = 0f
    private var mFrom = 0
    private var mFromDragPercent = 0f
    private val mAnimateToCorrectPosition: Animation = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val targetTop: Int
            val endTarget: Int = totalDragDistance
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mTarget!!.top
            mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime
            mBaseRefreshView!!.setPercent(mCurrentDragPercent, false)
            setTargetOffsetTop(offset /* requires update */)
        }
    }
    private var mNotify = false
    private var mListener: OnRefreshListener? = null
    private var mTargetPaddingTop = 0
    private var mTargetPaddingBottom = 0
    private var mTargetPaddingRight = 0
    private var mTargetPaddingLeft = 0
    private val mAnimateToStartPosition: Animation = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    fun setRefreshStyle(type: Int) {
        setRefreshing(false)
        mBaseRefreshView = when (type) {
            STYLE_SUN -> SunRefreshView(this)
            else -> throw InvalidParameterException("Type does not exist")
        }
        mRefreshView.setImageDrawable(mBaseRefreshView)
    }

    /**
     * This method sets padding for the refresh (progress) view.
     */
    fun setRefreshViewPadding(left: Int, top: Int, right: Int, bottom: Int) {
        mRefreshView.setPadding(left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        ensureTarget()
        if (mTarget == null) return
        val measuredWidth = MeasureSpec.makeMeasureSpec(measuredWidth - paddingRight - paddingLeft, MeasureSpec.EXACTLY)
        val measuredHeight = MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, MeasureSpec.EXACTLY)
        mTarget!!.measure(measuredWidth, measuredHeight)
        mRefreshView.measure(measuredWidth, measuredHeight)
    }

    private fun ensureTarget() {
        if (mTarget != null) return
        if (childCount > 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child !== mRefreshView) {
                    mTarget = child
                    mTargetPaddingBottom = mTarget!!.paddingBottom
                    mTargetPaddingLeft = mTarget!!.paddingLeft
                    mTargetPaddingRight = mTarget!!.paddingRight
                    mTargetPaddingTop = mTarget!!.paddingTop
                }
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled || canChildScrollUp() || mRefreshing) {
            return false
        }
        val action = MotionEventCompat.getActionMasked(ev)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTop(0)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                val initialMotionY = getMotionEventY(ev, mActivePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                mInitialMotionY = initialMotionY
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }
                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - mInitialMotionY
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }
        return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev)
        }
        val action = MotionEventCompat.getActionMasked(ev)
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val yDiff = y - mInitialMotionY
                val scrollTop = yDiff * DRAG_RATE
                mCurrentDragPercent = scrollTop / totalDragDistance
                if (mCurrentDragPercent < 0) {
                    return false
                }
                val boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent))
                val extraOS = Math.abs(scrollTop) - totalDragDistance
                val slingshotDist = totalDragDistance.toFloat()
                val tensionSlingshotPercent = Math.max(0f,
                        Math.min(extraOS, slingshotDist * 2) / slingshotDist)
                val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow(
                        (tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
                val extraMove = slingshotDist * tensionPercent / 2
                val targetY = (slingshotDist * boundedDragPercent + extraMove).toInt()
                mBaseRefreshView!!.setPercent(mCurrentDragPercent, true)
                setTargetOffsetTop(targetY - mCurrentOffsetTop)
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                mIsBeingDragged = false
                if (overScrollTop > totalDragDistance) {
                    setRefreshing(true, true)
                } else {
                    mRefreshing = false
                    animateOffsetToStartPosition()
                }
                mActivePointerId = INVALID_POINTER
                return false
            }
        }
        return true
    }

    private fun animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop
        mFromDragPercent = mCurrentDragPercent
        val animationDuration = Math.abs((MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent).toLong())
        mAnimateToStartPosition.reset()
        mAnimateToStartPosition.duration = animationDuration
        mAnimateToStartPosition.interpolator = mDecelerateInterpolator
        mAnimateToStartPosition.setAnimationListener(mToStartListener)
        mRefreshView.clearAnimation()
        mRefreshView.startAnimation(mAnimateToStartPosition)
    }

    private fun animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop
        mFromDragPercent = mCurrentDragPercent
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = MAX_OFFSET_ANIMATION_DURATION.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        mRefreshView.clearAnimation()
        mRefreshView.startAnimation(mAnimateToCorrectPosition)
        if (mRefreshing) {
            mBaseRefreshView!!.start()
            if (mNotify) {
                if (mListener != null) {
                    mListener!!.onRefresh()
                }
            }
        } else {
            mBaseRefreshView!!.stop()
            animateOffsetToStartPosition()
        }
        mCurrentOffsetTop = mTarget!!.top
        mTarget!!.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, totalDragDistance)
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom - (mFrom * interpolatedTime).toInt()
        val targetPercent = mFromDragPercent * (1.0f - interpolatedTime)
        val offset = targetTop - mTarget!!.top
        mCurrentDragPercent = targetPercent
        mBaseRefreshView!!.setPercent(mCurrentDragPercent, true)
        mTarget!!.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop)
        setTargetOffsetTop(offset)
    }

    fun setRefreshing(refreshing: Boolean) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                mBaseRefreshView!!.setPercent(1f, true)
                animateOffsetToCorrectPosition()
            } else {
                animateOffsetToStartPosition()
            }
        }
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(ev, activePointerId)
        return if (index < 0) {
            (-1).toFloat()
        } else MotionEventCompat.getY(ev, index)
    }

    private fun setTargetOffsetTop(offset: Int) {
        mTarget!!.offsetTopAndBottom(offset)
        mBaseRefreshView!!.offsetTopAndBottom(offset)
        mCurrentOffsetTop = mTarget!!.top
    }

    private fun canChildScrollUp(): Boolean {
        return ViewCompat.canScrollVertically(mTarget, -1)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        ensureTarget()
        if (mTarget == null) return
        val height = measuredHeight
        val width = measuredWidth
        val left = paddingLeft
        val top = paddingTop
        val right = paddingRight
        val bottom = paddingBottom
        mTarget!!.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop)
        mRefreshView.layout(left, top, left + width - right, top + height - bottom)
    }

    fun setOnRefreshListener(listener: OnRefreshListener?) {
        mListener = listener
    }

    interface OnRefreshListener {
        fun onRefresh()
    }

    companion object {
        const val STYLE_SUN = 0
        const val MAX_OFFSET_ANIMATION_DURATION = 700
        private const val DRAG_MAX_DISTANCE = 120
        private const val DRAG_RATE = .5f
        private const val DECELERATE_INTERPOLATION_FACTOR = 2f
        private const val INVALID_POINTER = -1
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshView)
        val type = a.getInteger(R.styleable.PullToRefreshView_type, STYLE_SUN)
        a.recycle()
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        totalDragDistance = Utils.convertDpToPixel(context, DRAG_MAX_DISTANCE)
        mRefreshView = ImageView(context)
        setRefreshStyle(type)
        addView(mRefreshView)
        setWillNotDraw(false)
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)
    }
}