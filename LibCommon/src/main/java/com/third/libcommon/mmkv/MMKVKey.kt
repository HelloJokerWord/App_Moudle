package com.third.libcommon.mmkv

/**
 * key一定要等于value
 *
 * 避免重复
 */

object MMKVKey {

    /**
     * 登陆数据
     */
    const val LOGIN_INFO = "LOGIN_INFO"

    /**
     * 游客数据
     */
    const val GUEST_INFO = "GUEST_IFNO"

    /**
     * 是否显示new未看过的
     */
    const val LAST_GALLERY_WATCH_TIME = "LAST_GALLERY_WATCH_TIME"
    const val LAST_AVATAR_WATCH_TIME = "LAST_AVATAR_WATCH_TIME"

    /**
     * 键盘高度
     */
    const val KEYBOARD_HIGH = "KEYBOARD_HIGH"

    /**
     * 日志开关
     */
    const val KEY_LOG_ENABLE = "KEY_LOG_ENABLE"

    const val NO_CONFIRM_ORDER = "NO_CONFIRM_ORDER"

    /**
     * 首次启动展示引导页
     */
    const val KEY_IS_SHOW_GUIDE = "KEY_IS_SHOW_GUIDE"

    /**
     * 保存上次请求是否有新图生成的时间
     */
    const val KEY_LAST_REQ_COUNT = "KEY_LAST_REQ_COUNT"


}