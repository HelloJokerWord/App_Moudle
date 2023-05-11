package com.example.appmoudle.manager

import com.example.appmoudle.BuildConfig
import com.example.appmoudle.config.MMKVKeys
import com.third.libcommon.MMKVManager


/**
 * Created on 2022/9/29.
 * @author Joker
 * Des: 全局环境配置
 */

object EnvSwitchManager {

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
    fun isPublish() = MMKVManager.getBoolean(MMKVKeys.KEY_LOG_ENABLE, BuildConfig.FLAVOR == ENV_DEVELOP || BuildConfig.DEBUG)

    /**
     * 更新日志可视化
     */
    fun setLogEnable() = MMKVManager.put(MMKVKeys.KEY_LOG_ENABLE, true)
}