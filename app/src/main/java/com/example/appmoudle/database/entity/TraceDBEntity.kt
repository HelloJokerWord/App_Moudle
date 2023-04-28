package com.example.appmoudle.database.entity

import com.blankj.utilcode.util.TimeUtils
import com.example.appmoudle.database.base.BaseDBEntity
import io.objectbox.annotation.Entity

/**
 * 数分埋点实体类
 */
@Entity
class TraceDBEntity : BaseDBEntity() {

    companion object {
        const val TYPE_BURIED = "Buried"
        const val TYPE_ANDROID = "AndroidLog"
    }

    var eid: String = ""                                //事件唯一ID
    var timestamp: Long = TimeUtils.getNowMills()       //发生时间戳
    var payload: String? = null                         //实际数据json

    var dataType: String? = null                        //自定义数据类型 ，自己埋的日志：AndroidLog , 数分埋的日志：Buried

    var timeNow: String? = TimeUtils.getNowString()

    override fun toString(): String {
        return "BuriedDBEntity(timeNow=$timeNow,dataType=$dataType，id=$id,unique=$unique, eid='$eid', timestamp=$timestamp, payload=$payload)"
    }
}