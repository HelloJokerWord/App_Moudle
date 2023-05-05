package com.example.appmoudle.agentweb

import android.net.http.SslError
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebView
import com.just.agentweb.AgentWebUIControllerImplBase

/**
 * 如果你需要修改某一个AgentWeb 内部的某一个弹窗 ，请看下面的例子
 * 注意写法一定要参照 DefaultUIController 的写法 ，因为UI自由定制，但是回调的方式是固定的，并且一定要回调。
 * 注释部分以后优化
 */
class WebUIController : AgentWebUIControllerImplBase() {

    override fun onShowMessage(message: String, from: String) {
        Log.i("BaseAgentWebFragment", "message:$message")
    }

    override fun onShowSslCertificateErrorDialog(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        Log.i("BaseAgentWebFragment", "error:${error?.url}")
    }
}