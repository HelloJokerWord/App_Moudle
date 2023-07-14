package com.libhttp.interceptor


import android.util.Log
import com.libhttp.HttpManager
import okhttp3.Interceptor
import okhttp3.Response


/**
 * Created on 2022/9/9.
 * @author Joker
 * Des: 重试请求拦截器
 */

class RetryInterceptor : Interceptor {

    companion object {
        private const val MAX_RETRY_COUNT = 2
    }

    private var retryNum = 0    //假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        //Log.i("RxHttp", "retryNum=$retryNum")
        var response = chain.proceed(request)
        while (!response.isSuccessful && retryNum < MAX_RETRY_COUNT) {
            retryNum++
            Log.w(HttpManager.TAG, "retryNum=$retryNum")
            response.close()
            response = chain.proceed(request)
        }
        return response
    }

}