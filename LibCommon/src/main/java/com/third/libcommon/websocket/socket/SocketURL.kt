package com.third.libcommon.websocket.socket

import com.third.libcommon.BuildConfig


/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:
 */

object SocketURL {

    private const val BASE_URL_DEV = "wss://testservice.happychathk.com/"       //测试环境
    private const val BASE_URL_PROD = "wss://service.happychathk.com/"          //正式环境

    private var baseSocketUrl = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PROD

    /**
     * 业务长链接地址
     */
    val URL_BUSINESS = baseSocketUrl + "infrastructure/connection/ws"
}