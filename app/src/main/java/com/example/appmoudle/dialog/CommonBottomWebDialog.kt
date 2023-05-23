package com.example.appmoudle.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import cc.taylorzhang.singleclick.onSingleClick
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.R
import com.example.appmoudle.agentweb.AgentWebBuilder
import com.example.appmoudle.agentweb.JsCMD
import com.example.appmoudle.agentweb.WebURL
import com.example.appmoudle.base.BaseBottomPopupView
import com.example.appmoudle.config.EventWeb
import com.example.appmoudle.databinding.DialogBottomWebBinding
import com.example.appmoudle.googlepay.GooglePayManager
import com.example.appmoudle.widget.RadiusWebView
import com.lxj.xpopup.XPopup
import com.third.libcommon.LiveEventManager
import com.weikaiyun.fragmentation.SupportFragment

/**
 * Description: 底部的列表对话框
 * Create by dance, at 2018/12/16
 */
class CommonBottomWebDialog(context: Context) : BaseBottomPopupView<DialogBottomWebBinding>(context) {

    override fun getImplLayoutId() = R.layout.dialog_bottom_web
    override fun getViewBinding() = DialogBottomWebBinding.bind(popupImplView)
    override fun getMaxHeight() = dialogHeight

    private val errorLayoutId = R.layout.layout_app_state_view_retry
    private val errorReLoadBtnId = R.id.layoutRetry
    private val webBGColor = R.color.color_white

    companion object {
        fun show(
            fragment: SupportFragment,
            webUrl: String,
            enableDrag: Boolean = true,         //是否支持下拉关闭
            dialogHeight: Int = ScreenUtils.getAppScreenHeight() * 2 / 3,  //弹窗高度，0为全屏默认设计高度
            webTitle: String = "",              //原生设置标题，没有则不显示
            isUseWebTitle: Boolean = false,     //是否使用web加载后的标题
            webViewTopRadius: Float = 0.0f,     //单位dp 传设计DP值即可
        ): CommonBottomWebDialog? {
            fragment.context?.let {
                val dialog = CommonBottomWebDialog(it).apply {
                    this.fragment = fragment
                    this.dialogHeight = dialogHeight
                    this.webViewTopRadius = webViewTopRadius
                    this.webTitle = webTitle
                    this.webUrl = webUrl
                    this.isUseWebTitle = isUseWebTitle
                }

                XPopup.Builder(it)
                    .enableDrag(enableDrag)
                    .asCustom(dialog)
                    .show()

                return dialog
            } ?: run {
                return null
            }
        }
    }

    private var fragment: SupportFragment? = null
    private var dialogHeight = 0
    private var webViewTopRadius = 0.0f
    private var webTitle = ""
    private var webUrl = ""
    private var isUseWebTitle = false
    private var mAgentWebBuilder: AgentWebBuilder? = null
    private val webCallBack = object : AgentWebBuilder.AgentBuildCallBack {
        override fun onAgentWebPageFinished(view: WebView?, url: String?) {
            Log.i(TAG, "onPageFinished  url = $url")
            dialogBinding?.pbLoading?.isVisible = false
        }

        override fun onAgentWebReceivedTitle(view: WebView?, title: String?) {
            Log.i(TAG, "onWebReceivedTitle title=$title")
            if (isUseWebTitle) {
                webTitle = title ?: ""
                dialogBinding?.layoutTitle?.tvTitle?.text = webTitle
            }
        }
    }

    private val observer = Observer<EventWeb> {
        when (it.action) {
            JsCMD.closeWebPage -> dismiss()

        }
    }

    override fun onCreate() {
        super.onCreate()
        dialogBinding = DialogBottomWebBinding.bind(popupImplView)
        dialogBinding?.layoutTitle?.apply {
            clTitle.isVisible = if (webTitle.isEmpty()) false else {
                tvTitle.text = webTitle
                true
            }
            ivBack.onSingleClick { dismiss() }
        }

        dialogBinding?.pbLoading?.isVisible = true
        initWebView()
        //注册h5调原生
        LiveEventManager.observeForever(EventWeb::class.java, observer)
    }

    override fun onDialogResume() {
        super.onDialogResume()
        mAgentWebBuilder?.onResume()
    }

    override fun onDialogPause() {
        super.onDialogPause()
        mAgentWebBuilder?.onPause()
    }

    override fun onDismiss() {
        GooglePayManager.onDestroy()
        mAgentWebBuilder?.onDestroy()
        LiveEventManager.removeObserve(EventWeb::class.java, observer)
        super.onDismiss()
    }

    /**
     * 初始化web加载到dialog
     */
    private fun initWebView() {
        if (fragment == null || dialogBinding == null) return

        mAgentWebBuilder = AgentWebBuilder()
        if (webUrl.contains(WebURL.TOP_UP_PAGE_URL)) {
            GooglePayManager.mAgentWebBuilder = mAgentWebBuilder
            GooglePayManager.checkOrderCache()
        }


        val webView = RadiusWebView(Utils.getApp())
        mAgentWebBuilder?.agentBuildCallBack = webCallBack
        mAgentWebBuilder?.onCreate(
            fragment = fragment!!,
            webContentView = dialogBinding!!.flContent,
            errorLayoutId = errorLayoutId,
            errorReLoadBtnId = errorReLoadBtnId,
            url = webUrl,
            webBGColor = webBGColor,
            webView = webView
        )
        webView.setTopRadius(ConvertUtils.dp2px(webViewTopRadius).toFloat())
        mAgentWebBuilder?.mAgentWeb?.webCreator?.webParentLayout?.background = ColorDrawable(Color.TRANSPARENT)
    }

}