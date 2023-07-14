package com.example.appmoudle.agentweb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import com.example.appmoudle.base.BaseFragment
import com.example.appmoudle.global.EventWeb
import com.example.appmoudle.databinding.FCommonWebBinding
import com.example.appmoudle.googlepay.GooglePayManager
import com.third.libcommon.LiveEventManager


/**
 * 作者: Joker
 * 时间: 2019/1/19. 15:11
 * 描述: web页面
 */
class CommonWebF : BaseAgentWebFragment<FCommonWebBinding>() {

    companion object {
        fun startWeb(fragment: BaseFragment, title: String? = "", url: String?, isShowTitle: Boolean = true): CommonWebF {
            val webF = CommonWebF()
            webF.arguments = Bundle().apply {
                putString("title", title)
                putString("url", url)
                putBoolean("isShowTitle", isShowTitle)
            }

            fragment.start(webF)
            return webF
        }
    }

    private var webTitle: String? = null
    private var url: String? = null
    private var isSetTitle = false   //原生是否设置了标题进来
    private var isShowTitle = true   //是否使用原生标题栏

    override fun getViewBinding(inflater: LayoutInflater) = FCommonWebBinding.inflate(inflater)
    override fun isNeedPaddingTop() = isShowTitle
    override fun getUrl() = url ?: ""
    override fun getWebContentView() = mViewBinding!!.flContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            webTitle = getString("title")
            url = getString("url")
            isShowTitle = getBoolean("isShowTitle")
            isSetTitle = !webTitle.isNullOrEmpty()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "isSetTitle=$isSetTitle webTitle=$webTitle url=$url")
        mViewBinding?.apply {
            flTitle.visibility = if (isShowTitle) View.VISIBLE else View.GONE
            tvTitle.text = "$webTitle"
            llLeft.setOnClickListener { if (mAgentWebBuilder?.agentWebBack() == false) pop() }
            //GooglePayManager.mAgentWebBuilder = mAgentWebBuilder

            checkOrderCache()
            //注册
            LiveEventManager.observe(this@CommonWebF, EventWeb::class.java) {
                when (it.action) {
                    JsCMD.closeWebPage -> pop()
                }
            }
        }
    }

    override fun onDestroy() {
        GooglePayManager.onDestroy()
        super.onDestroy()
    }

    override fun onWebReceivedTitle(view: WebView?, title: String?) {
        super.onWebReceivedTitle(view, title)
        if (!isSetTitle) webTitle = title
    }

    private fun checkOrderCache() {
        if (url?.contains(WebURL.TOP_UP_PAGE_URL) == true) {
             GooglePayManager.checkOrderCache()
        }
    }
}