package com.example.appmoudle.manager

import com.example.appmoudle.BuildConfig
import com.third.libcommon.MMKVManager


/**
 * Created on 2022/9/29.
 * @author Joker
 * Des: 全局日志开关
 */

object LogSwitchManager {

    private const val KEY_LOG_ENABLE = "KEY_LOG_ENABLE"

    /**
     * dev渠道：       测试包appId_dev 访问测试环境
     * publish渠道：   正式包appId     访问正式环境
     */
    private const val ENV_DEVELOP = "develop"

    /**
     * 是否是dev渠道
     */
    var isDev = BuildConfig.FLAVOR == ENV_DEVELOP

    /**
     * 获取日志enable
     */
    fun isLogEnable() = MMKVManager.getBoolean(KEY_LOG_ENABLE, BuildConfig.FLAVOR == ENV_DEVELOP || BuildConfig.DEBUG)

    /**
     * 更新日志可视化
     */
    fun setLogEnable() = MMKVManager.put(KEY_LOG_ENABLE, true)
}