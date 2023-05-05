package com.third.libcommon.log

import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.TimeUtils
import com.orhanobut.logger.LogAdapter

/**
 * CreateBy:Joker
 * CreateTime:2023/5/5 15:17
 * description：
 */
class MyDiskLogAdapter : LogAdapter {

    override fun isLoggable(priority: Int, tag: String?): Boolean = true

    override fun log(priority: Int, tag: String?, message: String) {
        //Log.i("DEBUG_TAG2", "$priority-$tag:$message\n")
        ThreadUtils.getIoPool().execute {
            val str = "${TimeUtils.getNowString()}-$priority:$message\n"
            FileIOUtils.writeFileFromString(LoggerManager.LOG_FILE_PATH, str, true)
        }
    }
}