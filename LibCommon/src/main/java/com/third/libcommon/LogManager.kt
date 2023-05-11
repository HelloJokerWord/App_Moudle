package com.third.libcommon

import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ThreadUtils
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack


/**
 * CreateBy:Joker
 * CreateTime:2023/5/5 14:22
 * description：
 */
object LogManager {

    private val LOG_PATH = "${PathUtils.getInternalAppCachePath()}/log"

    fun init() {
        LogUtils.getConfig().apply {
            isLogSwitch = BuildConfig.DEBUG                 //设置 log 总开关
            setConsoleSwitch(BuildConfig.DEBUG)             //设置 log 控制台开关
            globalTag = "DEBUG_LOG"                         //设置 log 全局 tag
//            isLogHeadSwitch = true                          //设置 log 头部信息开关
//            isLog2FileSwitch = true                         //设置 log 文件开关
            dir = LOG_PATH                                  //设置 log 文件存储目录
//            filePrefix = ""                                 //设置 log 文件前缀
//            setBorderSwitch(true)                           //设置 log 边框开关
//            isSingleTagSwitch = true                        //设置 log 单一 tag 开关（为美化 AS 3.1 的 Logcat）
//            setOnConsoleOutputListener { type, tag, content ->
//                Log.i("LogManager", "type=$type tag=$tag content=$content")
//            }
//            setOnFileOutputListener { filePath, content ->
//                Log.i("LogManager", "filePath=$filePath content=$content")
//            }
        }
    }

    fun deleteLogFile() {
        //设置文件删除策略
        ThreadUtils.getIoPool().execute {
            if (FileUtils.isFileExists(LOG_PATH)) {

            }

            FileUtils.delete(LOG_PATH)
        }
    }

    /**
     * 日志上传
     */
    fun uploadLogFile(owner: LifecycleOwner) {
        LogUtils.getLogFiles().forEach {
            HttpManager.uploadFile(owner, "", it, reqResult = object : RequestCallBack<String> {
                override fun onSuccess(data: String) {

                }

                override fun onFail(code: Int, msg: String?) {

                }
            })
        }
    }
}