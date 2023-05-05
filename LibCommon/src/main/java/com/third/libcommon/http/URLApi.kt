package com.third.libcommon.http

import com.third.libcommon.BuildConfig
import rxhttp.wrapper.annotation.DefaultDomain

/**
 * CreateBy:Joker
 * CreateTime:2023/5/4 17:08
 * description：
 */
object URLApi {

    const val BASE_URL_DEV = "https://testservice.happychathk.com"       //测试环境
    const val BASE_URL_PROD = "https://service.happychathk.com"          //正式环境

    @JvmField
    @DefaultDomain //设置为默认域名
    var baseUrl = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PROD

    const val UPLOAD_URL = "https://upload.happychathk.com"

}