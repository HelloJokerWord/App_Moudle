package com.example.appmoudle.startup

import android.content.Context
import com.blankj.utilcode.util.DeviceUtils
import com.example.appmoudle.database.DBManager
import com.example.appmoudle.global.GlobalUserManager
import com.example.appmoudle.manager.LogManager
import com.libgoogle.login.TwitterLoginManager
import com.rousetime.android_startup.AndroidStartup
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.tencent.bugly.crashreport.CrashReport.putUserData
import com.tencent.bugly.crashreport.CrashReport.setUserId
import com.third.libcommon.LiveEventManager
import com.third.libcommon.SvgaManager
import com.third.libcommon.constant.GlobalConstant
import com.third.libcommon.mmkv.MMKVManager


/**
 * CreateBy:Joker
 * CreateTime:2023/4/28 14:26
 * description：三方库
 */
class ThirdLibStartup : AndroidStartup<String>() {

    override fun callCreateOnMainThread(): Boolean = true

    override fun waitOnMainThread(): Boolean = true

    override fun create(context: Context): String? {
        //数据存储
        MMKVManager.init(context)

        //日志库初始化
        LogManager.init()

        //奔溃日志
        CrashReport.initCrashReport(context, GlobalConstant.BUGLY_APP_ID, false, UserStrategy(context).apply {
            deviceID = GlobalConstant.deviceTag
            deviceModel = DeviceUtils.getModel()
            setUserId(context, "${GlobalUserManager.getUid()}")
            putUserData(context, "uid", "${GlobalUserManager.getUid()}")
        })

        // 总线事件初始化
        LiveEventManager.init()

        //svga初始化
        SvgaManager.initSvga(context)

        //数据库初始化
        DBManager.init()

        //twitter登陆
        TwitterLoginManager.initTwitterLogin(context)

        return this.javaClass.name
    }

}