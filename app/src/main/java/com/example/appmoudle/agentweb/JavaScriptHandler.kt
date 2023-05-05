package com.example.appmoudle.agentweb

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.blankj.utilcode.util.ToastUtils
import com.example.appmoudle.config.EventWeb
import com.third.libcommon.LiveEventManager
import org.json.JSONObject

/**
 * 说明: js 调原生端操作
 */
class JavaScriptHandler internal constructor(webView: WebView) {

    companion object {
        private const val TAG = "JavaScriptHandler"
    }

    private val context: Context
    private val webView: WebView

    /**
     * js调原生
     * @param jsonStr 传递数据 jsonStr
     */
    @JavascriptInterface
    fun call(jsonStr: String?): String {
        Log.i(TAG, "jsonStr = $jsonStr")
        if (jsonStr.isNullOrEmpty()) return ""
        try {
            val jsonObj = JSONObject(jsonStr)
            val action = jsonObj.optString("action")
            val dataObj = jsonObj.optJSONObject("data")
            when (action) {
                JsCMD.closeWebPage -> LiveEventManager.post(EventWeb(JsCMD.closeWebPage))
                JsCMD.showToast -> {
                    val message = dataObj?.optString("message") ?: ""
                    ToastUtils.showShort(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    /**
     * 原生调JS
     * @param jsonStr   CallBackJsBean json
     */
    fun callJs(jsonStr: String?) {
        webView.post { webView.loadUrl("javascript:window.sendAppJsInfo($jsonStr)") }
    }


    init {
        context = webView.context
        this.webView = webView
    }
}