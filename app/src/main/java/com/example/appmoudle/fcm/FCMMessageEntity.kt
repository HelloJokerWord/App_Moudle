package com.example.appmoudle.fcm

import androidx.annotation.Keep

/**
 * Created on 2022/10/28.
 * @author Joker
 * Des:
 */
@Keep
data class FCMMessageEntity(
    val notification_type: Int,             //消息类型
    val notification_title: String?,        //标题
    val notification_content: String?,      //内容
    val notification_jump_url: String?,     //跳转连接 : https://wiki.inkept.cn/pages/viewpage.action?pageId=269118870
    val notification_receive_uid: Long,     //接收者id
    val notification_news_type: Int,        //消息发送类型1 应用系统内发送 2 后台发送
    val notification_task_id: String?,      //消息ID
)
