package com.example.appmoudle.agentweb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.viewbinding.ViewBinding
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseFragment
import com.gyf.immersionbar.ImmersionBar


/**
 * 作者: Joker
 * 时间: 2019/2/27. 11:36
 * 口号: 代码千万行，注释第一行；编码不规范，同事泪两行。
 * 描述: 视频播放详情基类
 */
abstract class BaseAgentWebFragment<VB : ViewBinding> : BaseFragment() {

    protected var mViewBinding: VB? = null
    protected var mAgentWebBuilder: AgentWebBuilder? = null
    private val webCallBack = object : AgentWebBuilder.AgentBuildCallBack {
        override fun onAgentWebPageFinished(view: WebView?, url: String?) {
            onWebPageFinished(view, url)
        }

        override fun onAgentWebReceivedTitle(view: WebView?, title: String?) {
            onWebReceivedTitle(view, title)
        }
    }

    private val webProgressColor = R.color.color_primary
    private val webProgressHigh = 2
    private val errorLayoutId = R.layout.layout_app_state_view_retry
    private val errorReLoadBtnId = R.id.layoutRetry
    private val webBGColor = R.color.color_white

    abstract fun getViewBinding(inflater: LayoutInflater): VB

    /**
     * 加载链接
     */
    abstract fun getUrl(): String

    /**
     * 设置装载web内容的ViewGroup容器
     */
    abstract fun getWebContentView(): ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = getViewBinding(inflater)
        return mViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated $TAG")
        if (isNeedPaddingTop()) {
            mViewBinding?.root?.setPadding(0, ImmersionBar.getStatusBarHeight(this), 0, 0)
        }

        mAgentWebBuilder = AgentWebBuilder()
        mAgentWebBuilder?.agentBuildCallBack = webCallBack
        mAgentWebBuilder?.onCreate(this, getWebContentView(), webProgressColor, webProgressHigh, errorLayoutId, errorReLoadBtnId, getUrl(), webBGColor)
    }

    override fun onResume() {
        mAgentWebBuilder?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWebBuilder?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWebBuilder?.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressedSupport(): Boolean {
        if (mAgentWebBuilder?.agentWebBack() == true) return true
        return super.onBackPressedSupport()
    }

    override fun pop() {
        try {
            super.pop()
            Log.i(TAG, "pop $TAG")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "pop $TAG  ${e.message}")
            //https://gz-testkm.inkept.cn/apm/crash/view/0/happychat/cabe766eb4ccb323bd5de25765e40ac7/2022-11-08/2022-11-09/0/2/
        }
    }

    /**
     * 配置沉浸式布局是否需要在状态栏之下 多层fragment时，用最内层即可，外层配置false
     */
    open fun isNeedPaddingTop() = true

    /**
     * 网页加载完毕
     */
    open fun onWebPageFinished(view: WebView?, url: String?) {}
    open fun onWebReceivedTitle(view: WebView?, title: String?) {}
}