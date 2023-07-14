package com.third.libcommon.constant

import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.DeviceUtils

/**
 * Created on 2022/9/15.
 * @author Joker
 * Des: app常量池 供全应用业务会用到的常量
 */

object GlobalConstant {

    /**
     * google登陆返回
     */
    const val REQ_CODE_GOOGLE_LOGIN = 10086

    /**
     * google更新返回
     */
    const val REQ_CODE_GOOGLE_UPDATE = 10087

    /**
     * google登陆错误
     */
    const val REQUEST_CODE_ERROR_DIALOG = 200

    /**
     * 官网地址
     */
    const val SHARE_TEXT = "https://www.monetpro.ai/"

    /**
     * 官方反馈邮箱
     */
    const val SEND_EMAIL = "monetproservice@gmail.com"

    /**
     * bugly appId
     */
    const val BUGLY_APP_ID = "93d171e812"

    /**
     * 服务端请求sign
     */
    const val SIGN_KEY_DEV = "51a44aee3c80ad33e25dfd8fda9a4488"
    const val SIGN_KEY_PUBLISH = "708be50b2e0a01186637f8cc86859647"

    /**
     * 唯一设备标识符号
     */
    val deviceTag = "${DeviceUtils.getAndroidID()}${DeviceUtils.getUniqueDeviceId()}"

    /**
     * 图生图，文生图
     */
    const val RETRY_TIME = 50                         //轮询重试次数
    const val DELAY_TIME = 5L * TimeConstants.SEC     //每次轮询间隔
}