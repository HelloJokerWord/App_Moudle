package com.libweight.refresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.libweight.R
import com.opensource.svgaplayer.SVGAImageView
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.third.libcommon.SvgaManager

@SuppressLint("RestrictedApi")
class CommonRefreshFooter : FrameLayout, RefreshFooter {

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private var view: View? = null
    private var svgaImageView: SVGAImageView? = null

    private fun initView(context: Context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_loading, this)
        view?.setBackgroundResource(R.drawable.weight_shape_transparent)
        this.setBackgroundResource(R.drawable.weight_shape_transparent)
        svgaImageView = view?.findViewById(R.id.svga_loading)
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.PullUpToLoad -> {
                //上拉加载更多
                SvgaManager.startRefresh(svgaImageView)
            }

            RefreshState.LoadFinish -> {
                //加载更多结束
                SvgaManager.stopPlay(svgaImageView)
            }

            else -> {}
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        SvgaManager.stopPlay(svgaImageView)
        svgaImageView = null
        view = null
    }

    override fun getView(): View = this
    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate
    override fun setPrimaryColors(vararg colors: Int) {}
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {}
    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {}
    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {}
    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {}
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int = 0
    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}
    override fun isSupportHorizontalDrag(): Boolean = false
    override fun autoOpen(duration: Int, dragRate: Float, animationOnly: Boolean): Boolean = true
    override fun setNoMoreData(noMoreData: Boolean): Boolean = false
}