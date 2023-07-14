package com.example.appmoudle.startup

import android.app.Application
import android.content.Context
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.R
import com.rousetime.android_startup.AndroidStartup
import com.weikaiyun.fragmentation.FragmentationManager

/**
 * CreateBy:Joker
 * CreateTime:2023/4/28 14:26
 * description：
 */
class UIStartup : AndroidStartup<String>() {

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
                .setTextColor(ColorUtils.getColor(R.color.color_toast_text))
                .setBgResource(R.color.color_toast_bg)
        }

        //初始化fragment管理工具
        FragmentationManager.init()

        return this.javaClass.name
    }

}