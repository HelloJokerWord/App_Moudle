package com.example.appmoudle.config

/**
 * key一定要等于value
 *
 * 避免重复
 */

object MMKVKeys {

    /**
     * 登陆数据
     */
    const val LOGIN_INFO = "LOGIN_INFO"

    /**
     * 登陆凭证
     */
    const val LOGIN_SESSION = "LOGIN_SESSION"

    /**
     * 当前登陆类型
     */
    const val LOGIN_TYPE = "LOGIN_TYPE"

    /**
     * 日志是否可见
     */
    const val LOG_ENABLE = "LOG_ENABLE"

    /**
     * 键盘高度
     */
    const val KEYBOARD_HIGH = "KEYBOARD_HIGH"

    /**
     * 消息页面 - 会话列表游标
     */
    const val CURSOR_MESSAGE = "CURSOR_MESSAGE"

    /**
     * 余额数据
     */
    const val MY_BALANCE = "MY_BALANCE"

    /**
     * 未确认的订单列表
     */
    const val NO_CONFIRM_ORDER = "NO_CONFIRM_ORDER"

    /**
     * 保存是否首次安装
     */
    const val IS_FIRST_INSTALL = "IS_FIRST_INSTALL"

    /**
     * 保存上一次检查通知栏权限时间
     */
    const val LAST_CHECK_NOTIFICATION_TIME = "LAST_CHECK_NOTIFICATION_TIME"

    /**
     * 保存是否已全量同步完会话列表过
     */
    const val IS_UPDATE_ALL_CONVERSATION = "IS_UPDATE_ALL_CONVERSATION"

    /**
     * 亲密度门槛
     */
    const val TELEPHONE_THRESHOLD = "TELEPHONE_THRESHOLD"

    /**
     * 上一次选中的录音
     */
    const val SAY_HELLO_LAST_VOICE = "SAY_HELLO_LAST_VOICE"

    /**
     * 上一次选中的文本
     */
    const val SAY_HELLO_LAST_TEXT = "SAY_HELLO_LAST_TEXT"

    /**
     * 自己语音房间id
     */
    const val MY_CHAT_ROOM_ID = "MY_CHAT_ROOM_ID"

    /**
     * 是否第一次进入自己的语音房
     */
    const val IS_FIRST_ENTER_MINE_CHAT_ROOM = "IS_FIRST_ENTER_MINE_CHAT_ROOM"

    /**
     * AF 数据
     */
    const val AF_MEDIA_SOURCE = "AF_MEDIA_SOURCE"

    /**
     * AF 数据
     */
    const val AF_CAMPAIGN = "AF_CAMPAIGN"

}