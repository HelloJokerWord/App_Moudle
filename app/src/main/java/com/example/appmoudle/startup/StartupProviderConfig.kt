package com.example.appmoudle.startup

import android.util.Log
import com.rousetime.android_startup.StartupListener
import com.rousetime.android_startup.model.CostTimesModel
import com.rousetime.android_startup.model.LoggerLevel
import com.rousetime.android_startup.model.StartupConfig
import com.rousetime.android_startup.provider.StartupProviderConfig

class StartupProviderConfig : StartupProviderConfig {

    override fun getConfig(): StartupConfig = StartupConfig.Builder()
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
}