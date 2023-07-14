package com.example.appmoudle

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.example.appmoudle.startup.NetStartup
import com.example.appmoudle.startup.ThirdLibStartup
import com.example.appmoudle.startup.UIStartup
import com.rousetime.android_startup.StartupListener
import com.rousetime.android_startup.StartupManager
import com.rousetime.android_startup.model.CostTimesModel
import com.rousetime.android_startup.model.LoggerLevel
import com.rousetime.android_startup.model.StartupConfig

/**
 * CreateBy:Joker
 * CreateTime:2023/4/28 14:14
 * description：
 */
class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initStartConfig()
    }

    /**
     * 初始化启动配置
     */
    private fun initStartConfig() {
        val config = StartupConfig.Builder()
            .setLoggerLevel(LoggerLevel.DEBUG)       // default LoggerLevel.NONE
            .setAwaitTimeout(12000L)                 // default 10000L
            .setOpenStatistics(true)                 // default true
            .setListener(object : StartupListener {
                override fun onCompleted(totalMainThreadCostTime: Long, costTimesModels: List<CostTimesModel>) {
                    // can to do cost time statistics.
                    Log.i("StartupTrack", "onCompleted:totalMainThreadCostTime=$totalMainThreadCostTime size=${costTimesModels.size}")
                }
            })
            .build()

        StartupManager.Builder()
            .setConfig(config)
            .addStartup(ThirdLibStartup())
            .addStartup(UIStartup())
            .addStartup(NetStartup())
            .build(this)
            .start()
            .await()
    }


}