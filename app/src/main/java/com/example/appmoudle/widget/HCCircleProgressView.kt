package com.example.appmoudle.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.example.appmoudle.R
import com.third.libcommon.extension.safeLet


/**
 * Created on 2022/12/21.
 *
 * @author Joker
 * Des:
 */
class HCCircleProgressView : View {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
        initData(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
        initData(context, attrs)
    }

    companion object {
        /**
         * 整圆进度条
         */
        const val TYPE_CIRCLE = 0

        /**
         * 切割进度条
         */
        const val TYPE_CLIP = 1
    }

    /**
     * 进度条类型
     */
    private var mProgressType = TYPE_CIRCLE

    /**
     * 进度条动画持续时间
     */
    private var mDuration: Int = 0

    /**
     * 是否显示进度条动画
     */
    private var mShowAnimation: Boolean = false

    /**
     * 进度条颜色
     */
    private var mProgressColor: Int = 0

    /**
     * 当前进度
     */
    private var mProgress = 0f

    /**
     * 进度条宽度
     */
    private var mProgressWidth: Int = 0

    /**
     * 进度条起始角度
     */
    private var mStartAngle: Int = 0

    /**
     * 进度条终止角度
     */
    private var mEndAngle: Int = 0

    /**
     * 进度条背景颜色
     */
    private var mBackgroundColor: Int = 0

    private var mViewWidth = 0
    private val mDefaultWidth = ConvertUtils.dp2px(10f)
    private var mRectF: RectF? = null
    private var mListener: OnProgressChangedListener? = null
    private var mValueAnimator: ValueAnimator? = null
    private var mProgressPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTotalProgress = 0f

    private fun initData(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.HCCircleProgressView)
        mProgressWidth = array.getDimension(R.styleable.HCCircleProgressView_hc_progressWidth, mDefaultWidth.toFloat()).toInt()
        mProgressColor = array.getColor(R.styleable.HCCircleProgressView_hc_progressColor, Color.WHITE)
        mStartAngle = array.getInt(R.styleable.HCCircleProgressView_hc_startAngle, 0)
        mEndAngle = array.getInt(R.styleable.HCCircleProgressView_hc_startAngle, 360)
        mBackgroundColor = array.getColor(R.styleable.HCCircleProgressView_hc_backgroundColor, Color.WHITE)
        mShowAnimation = array.getBoolean(R.styleable.HCCircleProgressView_hc_animation, false)
        mDuration = array.getInt(R.styleable.HCCircleProgressView_hc_duration, 1000)
        array.recycle()

        mProgressPaint?.strokeWidth = mProgressWidth.toFloat()
        mProgressPaint?.color = mProgressColor

        mBackgroundPaint?.strokeWidth = mProgressWidth.toFloat()
        mBackgroundPaint?.color = mBackgroundColor
    }

    private fun init() {
        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mProgressPaint?.style = Paint.Style.STROKE
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint?.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        mViewWidth = width.coerceAtMost(height)
        setMeasuredDimension(mViewWidth, mViewWidth)
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val width: Int
        val size = MeasureSpec.getSize(widthMeasureSpec)
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        width = when (mode) {
            MeasureSpec.EXACTLY -> if (size < mProgressWidth) mProgressWidth else size
            MeasureSpec.AT_MOST -> mDefaultWidth * 2
            else -> ScreenUtils.getScreenWidth()
        }
        return width
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val height: Int
        val size = MeasureSpec.getSize(heightMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        height = when (mode) {
            MeasureSpec.EXACTLY -> if (size < mProgressWidth) mProgressWidth else size
            MeasureSpec.AT_MOST -> mDefaultWidth * 2
            else -> ScreenUtils.getScreenHeight()
        }
        return height
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mRectF = RectF((mProgressWidth / 2f), (mProgressWidth / 2f), (mViewWidth - mProgressWidth / 2f), (mViewWidth - mProgressWidth / 2f))
        safeLet(mProgressPaint, mBackgroundPaint, mRectF) { mProgressPaint, mBackgroundPaint, mRectF ->
            when (mProgressType) {
                TYPE_CIRCLE -> {
                    canvas?.drawCircle((mViewWidth / 2f), (mViewWidth / 2f), (mViewWidth / 2f - mProgressWidth / 2f), mBackgroundPaint)
                    canvas?.drawArc(mRectF, mStartAngle.toFloat(), mProgress * 360f / 100f, false, mProgressPaint)
                }
                TYPE_CLIP -> {
                    canvas?.drawArc(mRectF, mStartAngle.toFloat(), (mEndAngle - mStartAngle).toFloat(), false, mBackgroundPaint)
                    canvas?.drawArc(mRectF, mStartAngle.toFloat(), mProgress * 360f / 100f, false, mProgressPaint)
                }
                else -> {}
            }
        }
    }

    /**
     * 设置进度条颜色
     */
    fun setProgressColor(progressColor: Int) {
        mProgressColor = progressColor
        mProgressPaint?.color = mProgressColor
    }

    /**
     * @param progress      进度
     * @param showAnimation 是否展示动画
     */
    fun setProgress(progress: Float, showAnimation: Boolean = false) {
        var newProgress = progress
        mShowAnimation = showAnimation

        if (mProgressType == TYPE_CLIP) {
            newProgress = ((mEndAngle - mStartAngle) * 100 / 360.0f).toInt().toFloat()
            mTotalProgress = newProgress
        } else {
            mTotalProgress = 100f
        }

        stopProgressAnimator()

        if (mShowAnimation) {
            mValueAnimator = ValueAnimator.ofFloat(newProgress).apply {
                duration = mDuration.toLong()
                interpolator = LinearInterpolator()
                addUpdateListener { animation: ValueAnimator ->
                    mProgress = animation.animatedValue as Float
                    mListener?.onProgressChanged(mProgress * 100 / mTotalProgress)
                    invalidate()
                }
                start()
            }
        } else {
            mProgress = newProgress
            invalidate()
        }
    }

    fun isProgressAnimatorRunning() = mValueAnimator?.isRunning == true

    fun stopProgressAnimator() {
        if (mValueAnimator?.isRunning == true) {
            mValueAnimator?.cancel()
        }
    }

    /**
     * 设置动画持续时间
     */
    fun setDuration(duration: Int) {
        mDuration = duration
    }

    /**
     * 设置进度进度条宽度
     */
    fun setProgressWidth(progressWidth: Int) {
        mProgressWidth = progressWidth
        mBackgroundPaint?.strokeWidth = progressWidth.toFloat()
        mProgressPaint?.strokeWidth = progressWidth.toFloat()
    }

    /**
     * 设置进度起始角度
     */
    fun setStartAngle(startAngle: Int) {
        mStartAngle = startAngle
    }

    /**
     * 设置进度条类型：[HCCircleProgressView.TYPE_CIRCLE]、[HCCircleProgressView.TYPE_CLIP]
     */
    fun setProgressType(progressType: Int) {
        mProgressType = progressType
    }

    /**
     * 设置切割圆结束角度
     */
    fun setEndAngle(endAngle: Int) {
        mEndAngle = endAngle
    }

    /**
     * 进度条开始、结束形状
     */
    fun setCap(cap: Cap?) {
        mProgressPaint?.strokeCap = cap
        mBackgroundPaint?.strokeCap = cap
    }

    /**
     * 设置背景圆颜色
     */
    fun setBackgroundCircleColor(backgroundColor: Int) {
        mBackgroundColor = backgroundColor
        mBackgroundPaint?.color = mBackgroundColor
        invalidate()
    }

    /**
     * 设置进度监听
     */
    fun setOnProgressChangedListener(listener: OnProgressChangedListener?) {
        mListener = listener
    }

    interface OnProgressChangedListener {
        fun onProgressChanged(currentProgress: Float)
    }
}