package com.example.appmoudle.config

import com.blankj.utilcode.util.DeviceUtils

/**
 * Created on 2022/9/15.
 * @author Joker
 * Des: app常量池 供全应用业务会用到的常量
 */

object HCConstant {

    /**
     * google登陆返回
     */
    const val REQ_CODE_GOOGLE_LOGIN = 10086

    /**
     * google更新返回
     */
    const val REQ_CODE_GOOGLE_UPDATE = 10087

    /**
     * 唯一设备标识符号
     */
    val deviceTag = "${DeviceUtils.getAndroidID()}${DeviceUtils.getUniqueDeviceId()}"

}