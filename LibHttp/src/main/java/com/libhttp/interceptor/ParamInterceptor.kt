package com.libhttp.interceptor

import com.blankj.utilcode.util.EncryptUtils
import com.libhttp.BuildConfig
import com.third.libcommon.constant.GlobalConstant
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created on 2022/9/9.
 * @author Joker
 * Des: 参数 拦截器
 */

class ParamInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url
//        val queryParam = requestUrl.query
        val requestNewBuilder = request.newBuilder()

        val keys = requestUrl.queryParameterNames.sorted()
        val map = mutableMapOf<String, Any?>()
        keys.forEach { key ->
            val value = requestUrl.newBuilder().build().queryParameter(key)
            if (!value.isNullOrEmpty()) map[key] = value
        }
        val sign = EncryptUtils.encryptMD5ToString(getSign(map)).lowercase()
        val newUrl = "$requestUrl&sign=$sign"
        if (newUrl.startsWith("http://") || newUrl.startsWith("https://")) requestNewBuilder.url(newUrl)

//        when (request.method) {
//            "GET" -> {
//                Log.i(TAG, "GET\n请求=$reqUrl\n参数=$queryParam")
//            }
//
//            "POST" -> {
//                val oldBodyRequest = request.body
//                val requestBuffer = Buffer()
//                oldBodyRequest?.writeTo(requestBuffer)
//                val oldBodyStr = requestBuffer.readUtf8()
//                requestBuffer.close()
//
//                Log.i(TAG, "POST\n请求=$reqUrl\n参数=$oldBodyStr")
//
//                requestNewBuilder.method(request.method, oldBodyRequest)
//            }
//        }

        return chain.proceed(requestNewBuilder.build())
    }

    /**
     * 公共参数
     */
    private fun getSign(sortMap: MutableMap<String, Any?>): String {
        var signStr = ""
        sortMap.forEach { (t, u) -> signStr += "$t=$u&" }
        signStr = signStr.substring(0, signStr.length - 1)
        signStr += if (BuildConfig.isPublish) GlobalConstant.SIGN_KEY_PUBLISH else GlobalConstant.SIGN_KEY_DEV
        return signStr
    }
}