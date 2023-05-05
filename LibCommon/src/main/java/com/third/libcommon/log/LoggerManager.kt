package com.third.libcommon.log

import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ThreadUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.third.libcommon.BuildConfig
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack
import java.io.File


/**
 * CreateBy:Joker
 * CreateTime:2023/5/5 14:22
 * description：
 */
object LoggerManager {

    val LOG_PATH = "${PathUtils.getInternalAppCachePath()}/log"
    val LOG_FILE_PATH = "${LOG_PATH}/log.txt"

    fun init() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) // (Optional) Whether to show thread info or not. Default true
            .methodCount(1) // (Optional) How many method line to show. Default 2
            .methodOffset(5) // (Optional) Hides internal method calls up to offset. Default 5
            //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag("DEBUG_LOG") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        Logger.addLogAdapter(MyDiskLogAdapter())
    }

    fun deleteLogFile() {
        //设置文件删除策略
        ThreadUtils.getIoPool().execute {
            if (FileUtils.getLength(File(LOG_FILE_PATH)) > 500L) {

            }

            FileUtils.delete(LOG_PATH)
        }
    }

    fun uploadLogFile(owner: LifecycleOwner) {
        val logFile = FileUtils.getFileByPath(LOG_FILE_PATH)
        HttpManager.uploadFile(owner, "", logFile, reqResult = object : RequestCallBack<String> {
            override fun onSuccess(data: String) {
                deleteLogFile()
            }

            override fun onFail(code: Int, msg: String?) {

            }
        })
    }
}