package com.libhttp

import rxhttp.wrapper.annotation.DefaultDomain

/**
 * CreateBy:Joker
 * CreateTime:2023/5/4 17:08
 * description：
 */
object URLApi {

    private const val BASE_URL_DEV = "http://cloudac-test.monetpro.ai"           //测试环境
    private const val BASE_URL_PROD = "https://cloudac.monetpro.ai"              //正式环境

    @JvmField
    @DefaultDomain //设置为默认域名
    var baseUrl = if (BuildConfig.isPublish) BASE_URL_PROD else BASE_URL_DEV

    /**
     * 文件下载
     */
    const val URL_RES_FILE = "/file"

    /**
     * fcmToken上传
     */
    const val URL_POST_FCM_TOKEN = "/file"

    /**
     * 图片上传接口
     */
    const val UPLOAD_IMAGE_URL = "/monet/resourceserv/upload"

    /**
     * 启动app获取相关id
     */
    const val URL_GUEST_INIT = "/monet/loginserv/guest/init"

    /**
     * 用户登陆
     */
    const val URL_EMAIL_LOGIN = "/monet/loginserv/user/login"

    /**
     * 三方登陆
     */
    const val URL_THIRD_LOGIN = "/monet/loginserv/user/thirdLogin"

    /**
     * 用户注册
     */
    const val URL_REGISTER = "/monet/loginserv/user/register"

    /**
     * 忘记密码，验证邮箱
     */
    const val URL_CHECK_EMAIL = "/monet/loginserv/user/forget/pass"

    /**
     * 验证邮箱验证码
     */
    const val URL_CHECK_VERIFY_CODE = "/monet/loginserv/user/modify/pass/verify"

    /**
     * 修改密码
     */
    const val URL_CHANGE_PASSWORD = "/monet/loginserv/user/modify/pass"

    /**
     * 账号注销
     */
    const val URL_DELETE_ACCOUNT = "/monet/loginserv/user/unregister"

    /**
     * 用户信息接口
     */
    const val URL_PROFILE_INFO = "/monet/monetserv/user/profile"

    /**
     * 头像列表
     */
    const val URL_AVATAR_LIST = "/monet/monetserv/user/aiAvatars/list"

    /**
     * 图库列表
     */
    const val URL_GALLERY_LIST = "/monet/monetserv/user/gallery/list"

    /**
     * 套餐接口
     */
    const val URL_PAY_MALL = "/monet/payserv/payment/packages"

    /**
     * 创建一次性消费订单接口
     */
    const val URL_CREATE_PAY_ORDER = "/monet/payserv/payItem/create"

    /**
     * 一次性消费订单确认
     */
    const val URL_CONFIRM_PAY_ORDER = "/monet/payserv/iap/google/verify"

    /**
     * 创建订阅订单接口
     */
    const val URL_CREATE_SUB_ORDER = "/monet/subscription/createOrder"

    /**
     * 订阅订单确认
     */
    const val URL_CONFIRM_SUB_ORDER = "/monet/subscription/clientCallback/google"

    /**
     * 检查是否有权限
     */
    const val URL_HAS_SUB_ABLE = "/monet/payserv/user/pic/authority"

    /**
     * 艺术风格列表
     */
    const val URL_ART_STYLE_LIST = "/monet/monetserv/config/style/list"

    /**
     * 提示词列表
     */
    const val URL_PROMPT_LIST = "/monet/monetserv/config/prompt/list"

    /**
     * 生成头像
     */
    const val URL_GENERATE_AVATAR = "/monet/monetserv/aigc/avatar"

    /**
     * 图生图
     */
    const val URL_GENERATE_IMAGE = "/monet/monetserv/aigc/imgToImg"

    /**
     * 文生图
     */
    const val URL_GENERATE_TEXT = "/monet/monetserv/aigc/textToImg"

    /**
     * 检查是否有新的图生成更新
     */
    const val URL_IS_NEW_IMG_CREATE = "/monet/monetserv/aigc/fetch"

    /**
     * 轮询图生图结果
     */
    const val URL_IMG_TO_IMG_RESULT = "/monet/monetserv/aigc/imgToImg/result"

    /**
     * 轮询文生图结果
     */
    const val URL_TEXT_TO_IMG_RESULT = "/monet/monetserv/aigc/textToImg/result"
}