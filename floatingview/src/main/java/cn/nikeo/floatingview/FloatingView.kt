package cn.nikeo.floatingview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.contains
import androidx.customview.widget.ViewDragHelper

class FloatingViewManager(private val activity: Activity) {

    private val activityContent: ViewGroup by lazy {
        activity.window.decorView.findViewById(android.R.id.content)
    }

    private val floatingViewParent: FloatingViewParent by lazy { FloatingViewParent(activity) }

    fun addFloatingView(
        floatingView: FloatingView,
        layoutParams: FrameLayout.LayoutParams,
        onFloatingViewClick: () -> Unit
    ) {
        floatingViewParent.setOnClickListener { onFloatingViewClick() }
        floatingViewParent.addView(floatingView, layoutParams)

        if (!activityContent.contains(floatingView)) {
            activityContent.addView(
                floatingViewParent, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    fun isFloatingViewAdded(floatingView: FloatingView): Boolean =
        floatingViewParent.contains(floatingView)

    fun removeFloatingView(floatingView: FloatingView) {
        if (floatingViewParent.contains(floatingView)) {
            floatingViewParent.removeView(floatingView)
        }
    }
}

private class FloatingViewParent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var mActionDown = false

    private var mContinueSliding = false

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var mDragger: ViewDragHelper
    private var mCallback: ViewDragCallback

    init {
        val density: Float = resources.displayMetrics.density
        val minVel: Float = MIN_FLING_VELOCITY * density

        mCallback = ViewDragCallback()
        mDragger = ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY, mCallback)
        mDragger.minVelocity = minVel
        mCallback.setDragger(mDragger)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun computeScroll() {
        if (mContinueSliding) {
            val draggerSettling = mDragger.continueSettling(true)
            if (draggerSettling) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDragger.processTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mActionDown = true
            }

            MotionEvent.ACTION_MOVE -> {
                mActionDown = false
            }

            MotionEvent.ACTION_UP -> {
                if (mActionDown) {
                    performClick()
                    mActionDown = false
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mActionDown = false
            }
        }
        return mDragger.isCapturedViewUnder(event.x.toInt(), event.y.toInt())
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return mDragger.shouldInterceptTouchEvent(event)
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {

        private lateinit var mDragger: ViewDragHelper

        fun setDragger(dragger: ViewDragHelper) {
            mDragger = dragger
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child is FloatingView
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            if (left < 0) return 0
            if (left > mWidth - child.width) return mWidth - child.width
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            if (top < 0) return 0
            if (top > mHeight - child.height) return mHeight - child.height
            return top
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val finalLeft = if (releasedChild.left < mWidth / 2) {
                0
            } else {
                mWidth - releasedChild.width
            }
            mContinueSliding =
                mDragger.smoothSlideViewTo(releasedChild, finalLeft, releasedChild.top)
            if (mContinueSliding) invalidate()
        }
    }

    companion object {
        private const val TOUCH_SLOP_SENSITIVITY: Float = 1F

        /**
         * Minimum velocity that will be detected as a fling
         */
        private const val MIN_FLING_VELOCITY: Int = 400 // dips per second
    }
}

open class FloatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }

    companion object {

        @JvmStatic
        fun image(context: Context, apply: ImageView.() -> Unit): FloatingView {
            return FloatingView(context).apply {
                addView(
                    ImageView(context).apply(apply),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }
}
