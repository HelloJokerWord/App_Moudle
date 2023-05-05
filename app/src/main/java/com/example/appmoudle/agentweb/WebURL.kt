package com.example.appmoudle.agentweb

import com.example.appmoudle.BuildConfig

/**
 * Created on 2022/7/7.
 * @author Joker
 * Des:存放访问url地址
 */

object WebURL {

    private const val BASE_URL_DEV_H5 = "https://testh5.happychathk.com"             //测试环境
    private const val BASE_URL_PROD_H5 = "https://h5.happychathk.com"                //正式环境

    var baseH5Url = if (BuildConfig.DEBUG) BASE_URL_DEV_H5 else BASE_URL_PROD_H5

    const val TOP_UP_PAGE_URL = "/finance/index.html"

    /**
     * 服务协议
     */
    fun urlUserProtocol(source: String) = "$baseH5Url/protocol/index.html#/user?source=$source"

    /**
     * 隐私协议
     */
    fun urlSecretProtocol(source: String) = "$baseH5Url/protocol/index.html#/privacy?source=$source"

}