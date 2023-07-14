package com.libhttp

import android.annotation.SuppressLint
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.Utils
import com.libhttp.interceptor.ParamInterceptor
import com.libhttp.interceptor.RetryInterceptor
import com.rxjava.rxlife.lifeOnMain
import com.third.libcommon.BuildConfig
import com.third.libcommon.constant.GlobalConstant
import com.third.libcommon.n8.N8Base64
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cache.CacheMode
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toObservableResponseData
import rxhttp.wrapper.ssl.HttpsUtils
import java.io.File
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSession

/**
 * CreateBy:Joker
 * CreateTime:2023/4/28 11:17
 * description：
 */
object HttpManager {

    /**
     * http请求超时配置
     */
    private const val READ_TIME_OUT_SECONDS = 60L
    private const val WRITE_TIME_OUT_SECONDS = 60L
    private const val TIME_OUT_SECONDS = 60L

    const val TAG = "HttpManager"
    private val mapCommonParams = mutableMapOf<String, Any?>()
    private var userParamsCallback: GetUserParamCallback? = null

    var okHttpClient: OkHttpClient? = null

    /**
     * http请求缓存配置
     */
    private val CACHE_PATH = "${PathUtils.getInternalAppCachePath()}/RxHttpCache"
    private const val CACHE_SIZE = 10L * 1024 * 1024
    private const val CACHE_TIME = 1L * TimeConstants.HOUR

    fun init(userParamsCallback: GetUserParamCallback? = null) {
        this.userParamsCallback = userParamsCallback
        //证书配置，可以访问所有 具体：https://github.com/liujingxing/rxhttp/wiki/%E5%85%B3%E4%BA%8EHttps
        val sslParams = HttpsUtils.getSslSocketFactory()
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .addInterceptor(ParamInterceptor())
            .addInterceptor(RetryInterceptor())
            .hostnameVerifier { hostname: String?, session: SSLSession? ->
                Log.i(TAG, "hostnameVerifier hostname=$hostname session=$session")
                true
            }
            .build()

        RxHttpPlugins.init(okHttpClient)                                      //自定义OkHttpClient对象
            .setDebug(BuildConfig.DEBUG, true, 2)    //是否开启调试模式，开启后，logcat过滤RxHttp，即可看到整个请求流程日志
            .setCache(File(CACHE_PATH), CACHE_SIZE, CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE, CACHE_TIME)
            //.setExcludeCacheKeys("time")                                    //设置一些key，不参与cacheKey的组拼
            //.setResultDecoder(s -> s)                                       //设置数据解密/解码器，非必须
            .setOnParamAssembly {                                             //设置公共参数，非必须
                //1、可根据不同请求添加不同参数，每次发送请求前都会被回调
                //2、如果希望部分请求不回调这里，发请求前调用RxHttp#setAssemblyEnabled(false)即可
//                val method = it.method
//                if (method.isGet) {         //Get请求
//                    it.add("method", "get")
//                } else if (method.isPost){ //Post请求
//                    it.add("method", "post")
//                }

                it.addHeader("platform", "android")          //添加公共请求头
                it.addAllQuery(getCommonParamMap())
            }
    }

    /**
     * get请求
     */
    inline fun <reified T> get(owner: LifecycleOwner, url: String, map: MutableMap<String, Any?>? = null, reqResult: RequestCallBack<T>? = null) {
        RxHttp.get(url)
            .addAll(map ?: mutableMapOf<String, Any?>())
            .toObservableResponseData<T>()
            .lifeOnMain(owner)
            .subscribe({ data -> reqResult?.onSuccess(data) }, { error -> onFail(error, reqResult) })
    }

    /**
     * get请求同步
     */
    inline fun <reified T> getSync(url: String, map: MutableMap<String, Any?>? = null, classType: Class<T>): T? {
        return RxHttp.get(url)
            .addAll(map ?: mutableMapOf<String, Any?>())
            .executeClass(classType)
    }

    /**
     * post表单提交
     */
    inline fun <reified T> postForm(owner: LifecycleOwner, url: String, map: MutableMap<String, Any?>? = null, reqResult: RequestCallBack<T>? = null) {
        RxHttp.postForm(url)
            .addAll(map ?: mutableMapOf<String, Any?>())
            .toObservableResponseData<T>()
            .lifeOnMain(owner)
            .subscribe({ data -> reqResult?.onSuccess(data) }, { error -> onFail(error, reqResult) })
    }

    /**
     * postJson提交
     */
    inline fun <reified T> postJson(owner: LifecycleOwner, url: String, map: MutableMap<String, Any?>? = null, reqResult: RequestCallBack<T>? = null) {
        RxHttp.postJson(url)
            .addAll(map ?: mutableMapOf<String, Any?>())
            .toObservableResponseData<T>()
            .lifeOnMain(owner)
            .subscribe({ data -> reqResult?.onSuccess(data) }, { error -> onFail(error, reqResult) })
    }

    /**
     * postBody提交
     * @param url           相对地址
     * @param map           参数
     */
    inline fun <reified T> postBody(owner: LifecycleOwner, url: String, map: MutableMap<String, Any?>? = null, reqResult: RequestCallBack<T>? = null) {
        RxHttp.postBody(url)
            .setBody(map ?: mutableMapOf<String, Any?>())
            .toObservableResponseData<T>()
            .lifeOnMain(owner)
            .subscribe({ data -> reqResult?.onSuccess(data) }, { error -> onFail(error, reqResult) })
    }

    /**
     * 文件路径上传 一般配合压缩路径提交
     */
    inline fun <reified T> uploadFilePath(owner: LifecycleOwner, url: String, file: String, map: MutableMap<String, Any?>? = null, progressListener: ProgressListener? = null, reqResult: RequestCallBack<T>? = null) {
        uploadFile(owner, url, mutableListOf(File(file)), map, progressListener, reqResult)
    }

    /**
     * 文件路径上传 一般配合压缩路径提交
     */
    inline fun <reified T> uploadFile(owner: LifecycleOwner, url: String, file: File, map: MutableMap<String, Any?>? = null, progressListener: ProgressListener? = null, reqResult: RequestCallBack<T>? = null) {
        uploadFile(owner, url, mutableListOf(file), map, progressListener, reqResult)
    }

    /**
     * 上传文件
     */
    inline fun <reified T> uploadFile(owner: LifecycleOwner, url: String, file: MutableList<File>, map: MutableMap<String, Any?>? = null, progressListener: ProgressListener? = null, reqResult: RequestCallBack<T>? = null) {
        RxHttp.postForm(url)
            .addAllQuery(map ?: mutableMapOf<String, Any?>())
            .addFiles("files", file)
            .toObservableResponseData<T>()
            .onMainProgress {
                //上传进度回调,0-100，仅在进度有更新时才会回调
                val currentProgress = it.progress   //当前进度 0-100
                val currentSize = it.currentSize    //当前已上传的字节大小
                val totalSize = it.totalSize        //要上传的总字节大小
                Log.d(TAG, "currentProgress=$currentProgress currentSize=$currentSize totalSize=$totalSize")
                progressListener?.onProgress(currentProgress, currentSize, totalSize)
            }
            .lifeOnMain(owner)    //页面销毁，自动关闭请求
            .subscribe({ data -> reqResult?.onSuccess(data) }, { error -> onFail(error, reqResult) })
    }

    /**
     * 文件下载
     * @param destPath a/b/c.apk
     * @param isAppend 是否断点下载
     */
    fun downloadFile(owner: LifecycleOwner, url: String, destPath: String, isAppend: Boolean = false, progressListener: ProgressListener? = null, reqResult: RequestCallBack<String>? = null) {
        RxHttp.get(url)
            .toDownloadObservable(destPath, isAppend)
            .onMainProgress {
                val currentProgress = it.progress   //当前进度 0-100
                val currentSize = it.currentSize    //当前已下载的字节大小
                val totalSize = it.totalSize        //要下载的总字节大小
                Log.d(TAG, "currentProgress=$currentProgress currentSize=$currentSize totalSize=$totalSize")
                progressListener?.onProgress(currentProgress, currentSize, totalSize)
            }
            .lifeOnMain(owner) //感知生命周期，并在主线程回调
            .subscribe({ data -> reqResult?.onSuccess(data) }, { error -> onFail(error, reqResult) })
    }

    inline fun <reified T> onFail(error: Throwable, reqResult: RequestCallBack<T>? = null) {
        try {
            if (error is ParseException) {
                reqResult?.onFail(error.errorCode.toInt(), "${error.message}")
            } else {
                reqResult?.onFail(-1, "${error.message}")
            }
        } catch (e: Exception) {
            e.printStackTrace()  //防止toInt错误
            reqResult?.onFail(-1, "${error.message}")
        }
    }

    /**
     * http请求公共参数
     */
    private fun getCommonParamMap(): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        //动态参数
        map["__user_id"] = userParamsCallback?.getUserId()
        map["__guest_id"] = userParamsCallback?.getGuestId()
        map["accessToken"] = userParamsCallback?.getUserToken()

        //静态参数
        if (!mapCommonParams.contains("__platform")) {
            mapCommonParams["__platform"] = "android"
            mapCommonParams["nnid4"] = getN8ID()
            mapCommonParams["__v"] = AppUtils.getAppVersionName()
            mapCommonParams["__la"] = LanguageUtils.getSystemLanguage().language
            mapCommonParams["country"] = LanguageUtils.getSystemLanguage().country
            mapCommonParams["__bm"] = DeviceUtils.getManufacturer()
            mapCommonParams["model"] = DeviceUtils.getModel()
            mapCommonParams["__ov"] = DeviceUtils.getSDKVersionName()
            mapCommonParams["deviceId"] = GlobalConstant.deviceTag
        }
        map.putAll(mapCommonParams)
        return map
    }

    /**
     * 公共参数
     */
    fun getCommonParamStr(): String {
        var commonParamStr = "?"
        getCommonParamMap().forEach { (t, u) -> commonParamStr += "&$t=$u" }
        return commonParamStr.replace(" ", "%20").replace("'", "%27")
    }

    @SuppressLint("HardwareIds")
    fun getN8ID(): String {
        var result = ""
        try {
            result = Settings.Secure.getString(Utils.getApp().contentResolver, Settings.Secure.ANDROID_ID)

            if (!result.isNullOrEmpty()) {
                result = N8Base64.encode(result)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }
}