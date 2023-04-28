package com.example.appmoudle.database.db

import com.blankj.utilcode.util.ThreadUtils
import com.example.appmoudle.database.base.BaseDB
import com.example.appmoudle.database.DBManager
import com.example.appmoudle.database.entity.TraceDBEntity
import com.example.appmoudle.database.entity.TraceDBEntity_
import io.objectbox.Property
import io.objectbox.query.QueryBuilder
import java.util.concurrent.Callable

/**
 * 用户相册存储资源类
 */
class TraceDB : BaseDB<TraceDBEntity>() {

    companion object {
        //线程安全单例
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { TraceDB() }
    }

    private val mBox by lazy { DBManager.createBox(TraceDBEntity::class) }

    override fun getBox() = mBox

    override fun getUniqueProperty(): Property<TraceDBEntity> = TraceDBEntity_.unique

    /**
     * 获取最旧的200条上报，上报完删除
     */
    fun getBuriedList(dataType: String, queryResult: (data: MutableList<TraceDBEntity>?) -> Unit) {
        DBManager.getDatabase().callInTxAsync(Callable<MutableList<TraceDBEntity>> {
            return@Callable mBox.query()
                .equal(TraceDBEntity_.dataType, dataType, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .orderDesc(TraceDBEntity_.timestamp)
                .build()
                .use { it.find(0, 200) }
        }) { result, error ->
            error?.printStackTrace()
            ThreadUtils.runOnUiThread { queryResult.invoke(result) }
        }
    }
}