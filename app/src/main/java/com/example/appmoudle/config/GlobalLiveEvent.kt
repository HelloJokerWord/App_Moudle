package com.example.appmoudle.config

import androidx.annotation.Keep
import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * CreateBy:Joker
 * CreateTime:2023/4/28 17:27
 * description：
 */

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
data class EventKeyBoardChange(val height: Int) : LiveEvent