package com.third.libcommon.http

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.rxjava.rxlife.lifeOnMain
import com.third.libcommon.BuildConfig
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
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

    private const val READ_TIME_OUT_SECONDS = 20L
    private const val WRITE_TIME_OUT_SECONDS = 20L
    private const val TIME_OUT_SECONDS = 10L
    const val TAG = "HttpManager"
    private val mapCommonParams = mutableMapOf<String, Any?>()

    fun init() {
        //证书配置，可以访问所有 具体：https://github.com/liujingxing/rxhttp/wiki/%E5%85%B3%E4%BA%8EHttps
        val sslParams = HttpsUtils.getSslSocketFactory()
        val clientLocal = OkHttpClient.Builder()
            .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            //.addInterceptor(ParamInterceptor())
            //.addInterceptor(RetryInterceptor())
            .hostnameVerifier { hostname: String?, session: SSLSession? ->
                Log.i("RxHttp", "hostnameVerifier hostname=$hostname session=$session")
                true
            }.build()

        RxHttpPlugins.init(clientLocal)                                       //自定义OkHttpClient对象
            .setDebug(BuildConfig.DEBUG, true, 2)      //是否开启调试模式，开启后，logcat过滤RxHttp，即可看到整个请求流程日志
            //.setCache(cacheFile, 1000 * 100, CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
            //.setExcludeCacheKeys("time")                                    //设置一些key，不参与cacheKey的组拼
            //.setResultDecoder(s -> s)                                       //设置数据解密/解码器，非必须
            .setOnParamAssembly {                                             //设置公共参数，非必须
                //1、可根据不同请求添加不同参数，每次发送请求前都会被回调
                //2、如果希望部分请求不回调这里，发请求前调用RxHttp#setAssemblyEnabled(false)即可
                val method = it.method
                if (method.isGet) {         //Get请求
                    it.add("method", "get")

                } else if (method.isPost) { //Post请求
                    it.add("method", "post")
                }

                getCommonParam().forEach { (key, value) -> it.add(key, value) }

                it.addHeader("platform", "android")          //添加公共请求头
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
    fun uploadFilePath(owner: LifecycleOwner, url: String, file: String, progressListener: ProgressListener? = null, reqResult: RequestCallBack<String>? = null) {
        uploadFile(owner, url, File(file), progressListener, reqResult)
    }

    /**
     * 上传文件
     */
    fun uploadFile(owner: LifecycleOwner, url: String, file: File, progressListener: ProgressListener? = null, reqResult: RequestCallBack<String>? = null) {
        RxHttp.postForm(url)
            .addFile("file", file)
            .toObservableString()
            .onMainProgress {
                //上传进度回调,0-100，仅在进度有更新时才会回调
                val currentProgress = it.progress   //当前进度 0-100
                val currentSize = it.currentSize    //当前已上传的字节大小
                val totalSize = it.totalSize        //要上传的总字节大小
                Log.i(TAG, "currentProgress=$currentProgress currentSize=$currentSize totalSize=$totalSize")
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
    fun downFile(owner: LifecycleOwner, url: String, destPath: String, isAppend: Boolean = false, progressListener: ProgressListener? = null, reqResult: RequestCallBack<String>? = null) {
        RxHttp.get(url)
            .toDownloadObservable(destPath, isAppend)
            .onMainProgress {
                val currentProgress = it.progress   //当前进度 0-100
                val currentSize = it.currentSize    //当前已下载的字节大小
                val totalSize = it.totalSize        //要下载的总字节大小
                Log.i(TAG, "currentProgress=$currentProgress currentSize=$currentSize totalSize=$totalSize")
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
    fun getCommonParam(): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        //动态参数
        map["uid"] = -1

        //静态参数
        if (!mapCommonParams.contains("platform")) {
            mapCommonParams["platform"] = "android"
            mapCommonParams["app_version"] = AppUtils.getAppVersionName()
            mapCommonParams["language"] = LanguageUtils.getSystemLanguage().language
            mapCommonParams["country"] = LanguageUtils.getSystemLanguage().country
            mapCommonParams["manufacturer"] = DeviceUtils.getManufacturer()
            mapCommonParams["model"] = DeviceUtils.getModel()
            mapCommonParams["unique"] = DeviceUtils.getUniqueDeviceId()
            mapCommonParams["system_version"] = DeviceUtils.getSDKVersionName()
        }
        map.putAll(mapCommonParams)
        return map
    }
}