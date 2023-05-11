package com.example.appmoudle.agentweb

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.BuildConfig
import com.just.agentweb.*
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.just.agentweb.filechooser.FileCompressor
import java.util.*

/**
 * Created on 2022/10/12.
 * @author Joker
 * Des: 整理公共webView配置  主要整合BaseAgentWebFragment 和 CommonBottomWebDialog 全屏和半屏
 */
class AgentWebBuilder {

    companion object {
        private const val TAG = "AgentWebBuilder"
    }

    var mAgentWeb: AgentWeb? = null
    var javaScriptHandler: JavaScriptHandler? = null
    var agentBuildCallBack: AgentBuildCallBack? = null

    private val fileCallBack = FileCompressor.FileCompressEngine { type, uri, callback ->
        Log.e(TAG, "compressFile type:$type")
        if ("system" == type) { // input/file 标签触发的文件选择，这种方式不存在性能问题，可压缩也可以不压缩，具体看自己业务要求
            callback.onReceiveValue(uri)
            return@FileCompressEngine
        }
    }

    /**
     * 创建初始化
     */
    fun onCreate(
        fragment: Fragment, webContentView: ViewGroup, webProgressColor: Int = 0, webProgressHigh: Int = 0,
        errorLayoutId: Int, errorReLoadBtnId: Int, url: String, webBGColor: Int, webView: WebView = WebView(Utils.getApp())
    ) {
        fragment.activity?.let {
            onCreate(it, webContentView, webProgressColor, webProgressHigh, errorLayoutId, errorReLoadBtnId, url, webBGColor, webView)
        }
    }

    fun onCreate(
        activity: Activity, webContentView: ViewGroup, webProgressColor: Int = 0, webProgressHigh: Int = 0,
        errorLayoutId: Int, errorReLoadBtnId: Int, url: String, webBGColor: Int, webView: WebView = WebView(Utils.getApp())
    ) {

        val indicatorBuilder = AgentWeb.with(activity).setAgentWebParent(webContentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))        //传入AgentWeb的父控件。
        val commonBuild = if (webProgressHigh == 0) {
            indicatorBuilder.closeIndicator()
        } else {
            indicatorBuilder.useDefaultIndicator(ColorUtils.getColor(webProgressColor), webProgressHigh)  //设置加载进度条颜色和高度
        }

        commonBuild.setPermissionInterceptor(getPermissionInterceptor())           //权限拦截
            .setAgentWebWebSettings(getWebSetting())                        //设置 IAgentWebSettings。
            .setWebViewClient(getWebViewClient())                           //与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
            .setWebChromeClient(getWebChromeClient())                       //WebChromeClient
            .setWebView(webView)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)   //打开其他应用时，弹窗咨询用户是否前往其他应用
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)            //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
            .setAgentWebUIController(WebUIController())                     //自定义UI  AgentWeb3.0.0 加入。
            .setMainFrameErrorView(errorLayoutId, errorReLoadBtnId)         //加载错误时页面 参数1：是错误显示的布局，参数2：点击刷新控件ID，-1：表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
            .useMiddlewareWebClient(getMiddleWareWebClient())               //设置WebViewClient中间件，支持多个WebViewClient， AgentWeb 3.0.0 加入。
            .useMiddlewareWebChrome(getMiddleWareWebChrome())               //设置WebChromeClient中间件，支持多个WebChromeClient，AgentWeb 3.0.0 加入。
            .additionalHttpHeader(url, "device", "android")           //设置请求投参数

        mAgentWeb = commonBuild.createAgentWeb()    //创建AgentWeb。
            .ready()                            //设置 WebSettings。
            .go(url)                       //WebView载入该url地址的页面并显示。

        FileCompressor.getInstance().registerFileCompressEngine(fileCallBack)

        //注册js通信
        webView.let {
            javaScriptHandler = JavaScriptHandler(it)
            it.addJavascriptInterface(javaScriptHandler!!, JsCMD.JS_NAME)
            it.setBackgroundColor(ColorUtils.getColor(webBGColor))
        }

        //刷新按钮点击触发
        // mAgentWeb.getWebCreator().getWebView().reload();

        if (BuildConfig.DEBUG) {
            AgentWebConfig.debug() //手机浏览器debug模式
        }
    }

    fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
    }

    fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
    }

    fun onDestroy() {
        agentBuildCallBack = null
        FileCompressor.getInstance().unregisterFileCompressEngine(fileCallBack)
        mAgentWeb?.webLifeCycle?.onDestroy()
    }

    /**
     * 页面返回
     */
    fun agentWebBack() = mAgentWeb?.back() == true

    /**
     * 重新加载当前页面
     */
    fun reloadWeb() = mAgentWeb?.webCreator?.webView?.reload()

    /**
     * PermissionInterceptor 能达到 url1 允许授权， url2 拒绝授权的效果。
     *
     * @return true 该Url对应页面请求权限进行拦截 ，false 表示不拦截。
     */
    private fun getPermissionInterceptor() = PermissionInterceptor { url: String, permissions: Array<String?>?, action: String ->
        Log.e(TAG, "getPermissionInterceptor url = $url permissions = ${Arrays.toString(permissions)}  action = $action")
        false
    }

    /**
     * 配置web设置
     */
    private fun getWebSetting(): IAgentWebSettings<WebSettings> {
        return object : AbsAgentWebSettings() {
            override fun bindAgentWebSupport(agentWeb: AgentWeb) {}
            override fun toSetting(webView: WebView): IAgentWebSettings<WebSettings> {
                settings(webView)
                return this
            }

            @Suppress("DEPRECATION")
            @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
            private fun settings(webView: WebView) {
                val mWebSettings = webView.settings
                mWebSettings.javaScriptEnabled = true
                mWebSettings.setSupportZoom(true)
                mWebSettings.builtInZoomControls = false
                mWebSettings.savePassword = false
                when {
                    //根据cache-control获取数据。
                    AgentWebUtils.checkNetwork(webView.context) -> {
                        mWebSettings.cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    //没网，则从本地获取，即离线加载
                    else -> {
                        mWebSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    }
                }
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        //适配5.0不允许http和https混合使用情况
                        mWebSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    }

                    Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT -> {
                        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    }
                }
                mWebSettings.textZoom = 100
                mWebSettings.databaseEnabled = true
                //mWebSettings.setAppCacheEnabled(true)
                mWebSettings.loadsImagesAutomatically = true
                mWebSettings.setSupportMultipleWindows(false)
                // 是否阻塞加载网络图片  协议http or https
                mWebSettings.blockNetworkImage = false
                // 允许加载本地文件html  file协议
                mWebSettings.allowFileAccess = true
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> {
                        // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
                        mWebSettings.allowFileAccessFromFileURLs = false
                        // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
                        mWebSettings.allowUniversalAccessFromFileURLs = false
                    }
                }
                mWebSettings.javaScriptCanOpenWindowsAutomatically = true
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        mWebSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                    }

                    else -> {
                        mWebSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                    }
                }
                mWebSettings.loadWithOverviewMode = false
                mWebSettings.useWideViewPort = false
                mWebSettings.domStorageEnabled = true
                mWebSettings.setNeedInitialFocus(true)
                mWebSettings.defaultTextEncodingName = "utf-8" //设置编码格式
                mWebSettings.defaultFontSize = 16
                mWebSettings.minimumFontSize = 12 //设置 WebView 支持的最小字体大小，默认为 8
                mWebSettings.setGeolocationEnabled(true)
                val dir = AgentWebConfig.getCachePath(webView.context)
                Log.i(TAG, "dir:" + dir + "   appcache:" + AgentWebConfig.getCachePath(webView.context))
                //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
                mWebSettings.setGeolocationDatabasePath(dir)
                mWebSettings.databasePath = dir
                //mWebSettings.setAppCachePath(dir)
                //缓存文件最大值
                //mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE)
                mWebSettings.userAgentString = "Model=" + Build.MODEL + ",SystemVersion=Android" + Build.VERSION.RELEASE + ",AppVersion="
                Log.i(TAG, "UserAgentString : " + mWebSettings.userAgentString)
            }
        }
    }

    /**
     * WebViewClient回调
     */
    private fun getWebViewClient() = object : WebViewClient() {}

    /**
     * WebChromeClient回调
     */
    private fun getWebChromeClient() = object : WebChromeClient() {}

    /**
     * MiddlewareWebChromeBase回调
     */
    private fun getMiddleWareWebChrome() = object : MiddlewareWebChromeBase() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            Log.i(TAG, "onReceivedTitle title = $title")
            agentBuildCallBack?.onAgentWebReceivedTitle(view, title)
        }
    }

    /**
     * MiddlewareWebClientBase回调
     */
    private fun getMiddleWareWebClient() = object : MiddlewareWebClientBase() {

        /**
         * 资源下载触发
         */
        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
            Log.d(TAG, "截断 url = ${request?.url}")
//                if (TextUtils.equals(netJs, request.getUrl().getPath())) {
//                    InputStream is = getResources().openRawResource(R.raw.statistic_js);
//                    return new WebResourceResponse("text/javascript", "utf-8", is);
//                } else if (TextUtils.equals(netCss, request.getUrl().getPath())) {
//                    InputStream is = getResources().openRawResource(R.raw.statistic_css);
//                    return new WebResourceResponse("text/css", "utf-8", is);
//                }
            return super.shouldInterceptRequest(view, request)
        }

        /**
         * 跳二级页面触发
         */
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            Log.i(TAG, "shouldOverrideUrlLoading url = ${request?.url}")
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.i(TAG, "onPageStarted   url = $url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.i(TAG, "onPageFinished  url = $url")
            agentBuildCallBack?.onAgentWebPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            Log.e(TAG, "onReceivedError code=${error?.errorCode} msg=${error?.description}")
        }

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            super.onReceivedHttpError(view, request, errorResponse)
            Log.e(TAG, "onReceivedHttpError code=${errorResponse?.statusCode}  msg=${errorResponse?.reasonPhrase}")
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed()
            super.onReceivedSslError(view, handler, error)
            Log.e(TAG, "onReceivedSslError ${error?.url} ${error?.primaryError}")
        }
    }

    interface AgentBuildCallBack {
        fun onAgentWebPageFinished(view: WebView?, url: String?)
        fun onAgentWebReceivedTitle(view: WebView?, title: String?)
    }
}