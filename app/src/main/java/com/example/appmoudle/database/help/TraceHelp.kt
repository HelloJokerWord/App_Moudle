package com.example.appmoudle.database.help

import androidx.lifecycle.LifecycleOwner
import com.example.appmoudle.database.db.TraceDB

/**
 * CreateBy:Joker
 * CreateTime:2023/5/23 14:22
 * description：
 */
class TraceHelp {
    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { TraceHelp() }
        private const val TAG = "TraceHelp"
    }

    /**
     * 上传日志
     */
    fun uploadErrorLog(owner: LifecycleOwner) {
        TraceDB.instance.getBuriedList("") { list ->
            if (list.isNullOrEmpty()) return@getBuriedList
            //查询成功

        }
    }


}