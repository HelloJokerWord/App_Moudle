package com.example.appmoudle.global

import com.example.appmoudle.login.bean.LoginEntity
import com.third.libcommon.LiveEventManager
import com.third.libcommon.mmkv.MMKVKey
import com.third.libcommon.mmkv.MMKVManager


/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:登陆信息 和 管理
 */

object GlobalUserManager {
    /**
     * 是否已登陆
     */
    fun isLogin(): Boolean = getUserInfo() != null

    /**
     * 本用户id
     */
    fun getUid(): Long = getUserInfo()?.uid ?: -1
    fun getAccessToken(): String = getUserInfo()?.access_token ?: ""

    /**
     * 保存登陆数据
     */
    fun saveLoginData(data: LoginEntity) = MMKVManager.put(MMKVKey.LOGIN_INFO, data)

    /**
     * 获取个人信息
     */
    fun getUserInfo(): LoginEntity? = MMKVManager.getParcelable(MMKVKey.LOGIN_INFO, LoginEntity::class.java)

    /**
     * 登出用户数据处理
     */
    fun loginOut() {
        MMKVManager.remove(MMKVKey.LOGIN_INFO)
        LiveEventManager.post(EventLoginOut())
    }
}