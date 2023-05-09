package com.example.appmoudle.startup

import android.app.Application
import android.content.Context
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.R
import com.example.appmoudle.database.DBManager
import com.third.libcommon.SvgaManager
import com.rousetime.android_startup.AndroidStartup
import com.third.libcommon.LiveEventManager
import com.third.libcommon.log.LogManager
import com.third.libcommon.MMKVManager
import com.third.libcommon.http.HttpManager
import com.weikaiyun.fragmentation.FragmentationManager

/**
 * CreateBy:Joker
 * CreateTime:2023/4/28 14:26
 * description：
 */
class CommonStartup : AndroidStartup<String>() {

    override fun callCreateOnMainThread(): Boolean = true

    override fun waitOnMainThread(): Boolean = true

    override fun create(context: Context): String? {
        //禁用黑暗模式,
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (context is Application) {
            //android常用方法
            Utils.init(context)

            //统一toast配置
            ToastUtils.getDefaultMaker()
                .setGravity(Gravity.CENTER, 0, 0)
                .setTextColor(ColorUtils.getColor(R.color.white_translucent_05))
                .setBgResource(R.drawable.hc_shape_black_a20_r12)
        }

        //数据存储
        MMKVManager.init(context)

        //日志库初始化
        LogManager.init()

        // 总线事件初始化
        LiveEventManager.init()

        //svga初始化
        SvgaManager.initSvga(context)

        //数据库初始化
        DBManager.init()

        //初始化fragment管理工具
        FragmentationManager.init()

        //网络请求配置初始化
        HttpManager.init()
        return this.javaClass.name
    }

}