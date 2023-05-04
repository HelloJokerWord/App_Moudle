package com.third.libcommon.http.interceptor



/**
 * Created on 2022/9/9.
 * @author Joker
 * Des: 参数 加密解密 拦截器
 */
//
//class ParamInterceptor : Interceptor {
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        if (request.method == "POST") {
//            val oldBodyRequest = request.body
//            val requestBuffer = Buffer()
//            oldBodyRequest?.writeTo(requestBuffer)
//            val oldBodyStr = putCommonParamsIfAbsent(requestBuffer.readUtf8())
//            requestBuffer.close()
//
//            val newRequestBody: RequestBody?
//            val reqUrl = request.url.toUri().path
//            val mediaType = (if (reqUrl.contains(URLApi.TAG_FILTER)) "application/json" else "text/plain").toMediaType()
//
//            Log.i("HttpManager", "请求=$reqUrl  参数=$oldBodyStr")
//
//            //文件上传过滤公共参数和加解密
//            newRequestBody = if (reqUrl.contains(URLApi.TAG_FILTER)) oldBodyRequest else {
//                HCEncryptUtil.encryptParam(oldBodyStr).toRequestBody(mediaType)
//            }
//
//            //构建新的request
//            val newRequest = request.newBuilder()
//                .addHeader("device", "android")
//                .addHeader("User-Agent", "Model=" + Build.MODEL + ",Android=" + Build.VERSION.RELEASE + ",versionCode=" + BuildConfig.VERSION_CODE)
//                .header("Content-type", mediaType.toString())
//                .header("Content-Length", newRequestBody?.contentLength().toString())
//                .method(request.method, newRequestBody)
//                .build()
//
//            var response = chain.proceed(newRequest)
//            val oldResponseBody = response.body
//            val oldResponseBodyStr = oldResponseBody?.string() ?: ""
//            val newResponseBodyStr = if (oldResponseBodyStr.isEmpty() || reqUrl.contains(URLApi.TAG_FILTER)) oldResponseBodyStr else HCEncryptUtil.decryptParam(oldResponseBodyStr)
//
//            Log.i("HttpManager", "响应=$newResponseBodyStr")
//            val responseBody = newResponseBodyStr.toResponseBody(mediaType)
//            response = response.newBuilder().body(responseBody).build()
//            return response
//        } else {
//            //构建新的request
//            val newRequest = request.newBuilder()
//                .addHeader("device", "android")
//                .addHeader("User-Agent", "Model=" + Build.MODEL + ",Android=" + Build.VERSION.RELEASE + ",versionCode=" + BuildConfig.VERSION_CODE)
//                .method(request.method, null)
//                .build()
//            return chain.proceed(newRequest)
//        }
//    }
//
//    private fun putCommonParamsIfAbsent(jsonString: String): String {
//        return try {
//            val jsonObj = if (jsonString.isEmpty()) JSONObject() else JSONObject(jsonString)
//            val commonParams = HttpManager.getCommonParam()
//            for ((k, v) in commonParams) {
//                if (!jsonObj.has(k)) jsonObj.put(k, v)
//            }
//            jsonObj.toString()
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            jsonString
//        }
//    }
//}