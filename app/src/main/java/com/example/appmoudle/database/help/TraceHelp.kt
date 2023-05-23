package com.example.appmoudle.database.help

import androidx.lifecycle.LifecycleOwner
import com.example.appmoudle.database.db.TraceDB
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack

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
            HttpManager.postBody(owner, "", mutableMapOf("list" to list), object : RequestCallBack<String?> {
                override fun onSuccess(data: String?) {
                    TraceDB.instance.remove(list) {
                        //删除成功
                    }
                }

                override fun onFail(code: Int, msg: String?) {

                }
            })
        }
    }


}