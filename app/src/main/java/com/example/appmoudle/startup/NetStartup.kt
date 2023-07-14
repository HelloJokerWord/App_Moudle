package com.example.appmoudle.startup

import android.content.Context
import com.example.appmoudle.global.GlobalUserManager
import com.libhttp.GetUserParamCallback
import com.libhttp.HttpManager
import com.rousetime.android_startup.AndroidStartup

/**
 * CreateBy:Joker
 * CreateTime:2023/4/28 14:26
 * description：网络库
 */
class NetStartup : AndroidStartup<String>() {

    override fun callCreateOnMainThread(): Boolean = false

    override fun waitOnMainThread(): Boolean = true

    override fun create(context: Context): String? {
        //网络请求配置初始化
        HttpManager.init(object : GetUserParamCallback {
            override fun getUserId() = GlobalUserManager.getUid()
            override fun getUserToken() = GlobalUserManager.getAccessToken()
            override fun getGuestId() = ""
        })
        return this.javaClass.name
    }

}