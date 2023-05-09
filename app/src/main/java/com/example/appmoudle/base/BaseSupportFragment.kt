package com.example.appmoudle.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.gyf.immersionbar.ImmersionBar

abstract class BaseSupportFragment<VB : ViewBinding> : BaseFragment() {

    protected var mViewBinding: VB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = getViewBinding(inflater)
        return mViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.d("$TAG onViewCreated")
        if (isNeedPaddingTop()) {
            val statusBarHigh = ImmersionBar.getStatusBarHeight(this)
            LogUtils.i("statusBarHigh=$statusBarHigh")
            getStatusPaddingView()?.setPadding(0, statusBarHigh, 0, 0)
        }
    }

    override fun onVisible() {
        super.onVisible()
        ImmersionBar.with(this).statusBarDarkFont(isDarkStatusBarFont()).init()

        activity?.window?.setSoftInputMode(if (isKeyboardCoverMode()) WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING else WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewBinding = null
    }

    abstract fun getViewBinding(inflater: LayoutInflater): VB?

    /**
     * 配置沉浸式布局是否需要在状态栏之下 多层fragment时，用最内层即可，外层配置false
     */
    open fun isNeedPaddingTop() = true

    /**
     * 配置状态栏顶部padding view
     */
    open fun getStatusPaddingView(): View? = mViewBinding?.root

    /**
     * 配置状态栏字体颜色
     */
    open fun isDarkStatusBarFont() = true

    /**
     * 键盘覆盖模式
     */
    open fun isKeyboardCoverMode() = false
}