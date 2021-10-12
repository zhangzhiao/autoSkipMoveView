package com.zza.moveview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import kotlin.math.sqrt


/**
 * @Author：created by zhangZhiAo
 * @CreateTime: 2021/10/12 13:40
 * @Describe: kotlin语言的移动代码
 */

class MoveViewToKt @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet? = null
) : View(context!!, attributeSet) {
    private var mIsDrug = true //判断是否是点击事件
    private var mCustomIsAttach = true //是否需要自动吸附
    private var mCustomIsDrag = true  // 是否可以拖拽

    private var mLastRawX = 0f //最终位置
    private var mLastRawY = 0f //最终位置
    private var mRootWindowMeasuredWidth = 0 //父布局的宽度
    private var mRootWindowMeasuredHeight = 0 //父布局的高度
    private var mRootTopY = 0 //父布局的顶部
    //用来规避的View 位置
    private val rectF: RectF = RectF(0f, 0f, 0f, 0f)

    /**
     * 这点很重要 我们要拦截他的事件的冒泡
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        super.dispatchTouchEvent(event)
        return true
    }

    /**
     * 设置所需跳过的view的上下左右边距（矩形）
     */
    fun setSkipViewRectF(viewRectF: RectF) {
        this.rectF.bottom = viewRectF.bottom
        this.rectF.top = viewRectF.top
        this.rectF.left = viewRectF.left
        this.rectF.right = viewRectF.right
    }

    /**
     * 接下来是重头戏
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mCustomIsDrag) {
            val mRawX = event?.rawX
            val mRawY = event?.rawY
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    mIsDrug = false
                    mLastRawX = mRawX!!
                    mLastRawY = mRawY!!
                    val viewGroup = parent as ViewGroup?
                    if (viewGroup != null) {
                        val location = IntArray(2)
                        viewGroup.getLocationInWindow(location)
                        mRootWindowMeasuredHeight = viewGroup.measuredHeight
                        mRootWindowMeasuredWidth = viewGroup.measuredWidth
                        mRootTopY = location[1]
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mRawX!! >= 0 && mRawX <= mRootWindowMeasuredWidth && mRawY!! >= mRootTopY && mRawY <= (mRootWindowMeasuredHeight + mRootTopY)) {
                        //手指X轴的滑动距离
                        val differenceValueX = mRawX - mLastRawX;
                        //手指Y轴的滑动距离
                        val differenceValueY = mRawY - mLastRawY;
                        //判断是否为拖动操作
                        if (!mIsDrug) {
                            mIsDrug =
                                sqrt(differenceValueX * differenceValueX + differenceValueY * differenceValueY) >= 2
                        }
                        //获取手指按下的距离与控件本身X轴的距离
                        val ownX = x
                        //获取手指按下的距离与控件本身Y轴的距离
                        val ownY = y
                        //理论中X轴拖动的距离
                        var endX = ownX + differenceValueX
                        //理论中Y轴拖动的距离
                        var endY = ownY + differenceValueY
                        //X轴可以拖动的最大距离
                        val maxX = mRootWindowMeasuredWidth - width.toFloat()
                        //Y轴可以拖动的最大距离
                        val maxY = mRootWindowMeasuredHeight - height.toFloat()
                        //X轴边界限制
                        endX = if (endX < 0f) {
                            0f
                        } else {
                            endX.coerceAtMost(maxX)
                        }
                        //Y轴边界限制
                        endY = if (endY < 0f) {
                            0f
                        } else {
                            endY.coerceAtMost(maxY)
                        }
                        //开始移动
                        x = endX
                        y = endY
                        //记录位置
                        mLastRawX = mRawX
                        mLastRawY = mRawY
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mCustomIsAttach) {
                        //判断是否为点击事件
                        if (mIsDrug) {
                            val center = (mRootWindowMeasuredWidth shr 1).toFloat()
                            //自动贴边
                            if (mLastRawX <= center) {
                                mLastRawX = 0f
                                animate()
                                    .setInterpolator(BounceInterpolator())
                                    .setDuration(1500)
                                    .x(mLastRawX)
                                    .start()
                            } else {
                                mLastRawX = (mRootWindowMeasuredWidth - width).toFloat()
                                animate()
                                    .setInterpolator(BounceInterpolator())
                                    .setDuration(1500)
                                    .x(mLastRawX)
                                    .start()
                            }
                            //这里是因为我所做的跳过的view是固定右边的 而且自动吸边 所以判断的是上面的mLastRawX
                            if (mLastRawX != 0f) {
                                val y = y + measuredHeight / 2
                                if (y >= rectF.top && y < rectF.bottom) {
                                    animate()
                                        .setInterpolator(BounceInterpolator())
                                        .setDuration(1500)
                                        .y(rectF.top - height)
                                        .start()
                                }
                            }
                        } else {
                            //这里可以处理点击事件
                        }
                    }

                }
            }
        }
        return if (mIsDrug) {
            mIsDrug
        } else {
            super.onTouchEvent(event)
        }

    }


}