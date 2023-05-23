package com.example.appmoudle.config

import androidx.annotation.Keep
import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * 网络变化
 */
@Keep
data class EventNetWorkChange(
    val isConnect: Boolean,             //是否连接
    val connectName: String? = null     //连接网路名称
) : LiveEvent


/**
 * 键盘变化
 */
@Keep
data class EventKeyBoardChange(
    val height: Int
) : LiveEvent

/**
 * h5调用
 */
@Keep
data class EventWeb(
    val action: String?,             //JS调原生操作指令
    val data: Any? = null            //操作数据
) : LiveEvent