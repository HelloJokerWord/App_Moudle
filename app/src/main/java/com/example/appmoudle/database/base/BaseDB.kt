package com.example.appmoudle.database.base

import android.util.Log
import com.blankj.utilcode.util.ThreadUtils
import com.example.appmoudle.database.DBManager

import io.objectbox.Box
import io.objectbox.Property
import io.objectbox.query.QueryBuilder
import java.util.concurrent.Callable


/**
 * Created on 2022/10/13.
 * @author Joker
 * Des:
 */

abstract class BaseDB<T> {
    companion object {
        const val TAG = "ObjectBox"
    }

    abstract fun getBox(): Box<T>

    abstract fun getUniqueProperty(): Property<T>

    /**
     * 根据 unique 参照
     * 添加或修改
     */
    fun put(entity: T, putResult: (() -> Unit)? = null) {
        put(arrayListOf(entity), putResult)
    }

    /**
     * 根据 unique 参照
     * 添加或修改
     */
    fun put(entities: List<T>, putResult: (() -> Unit)? = null) {
        if (entities.isEmpty()) {
            putResult?.invoke()
            return
        }
        DBManager.getDatabase().runInTxAsync({ getBox().put(entities) }) { _, error ->
            error?.printStackTrace()
            //Log.i(TAG, "put result=$result , error=$error")
            ThreadUtils.runOnUiThread { putResult?.invoke() }
        }
    }

    /**
     * 删除数据要是从查库查出来的，会包含id值
     */
    fun remove(entity: T, removeResult: (() -> Unit)? = null) {
        remove(arrayListOf(entity), removeResult)
    }

    /**
     * 删除数据要是从查库查出来的，会包含id值
     */
    fun remove(entities: List<T>, removeResult: (() -> Unit)? = null) {
        if (entities.isEmpty()) {
            removeResult?.invoke()
            return
        }
        DBManager.getDatabase().runInTxAsync({ getBox().remove(entities) }) { result, error ->
            error?.printStackTrace()
            Log.i(TAG, "remove result=$result , error=$error")
            ThreadUtils.runOnUiThread { removeResult?.invoke() }
        }
    }

    fun removeAll(removeResult: (() -> Unit)? = null) {
        DBManager.getDatabase().runInTxAsync({ getBox().removeAll() }) { result, error ->
            error?.printStackTrace()
            Log.i(TAG, "removeAll result=$result , error=$error")
            ThreadUtils.runOnUiThread { removeResult?.invoke() }
        }
    }

    fun count() = getBox().count()

    /**
     * 通过unique唯一标识获取
     */
    fun getByUnique(unique: String, queryResult: ((data: T?) -> Unit)) {
        DBManager.getDatabase().callInTxAsync(Callable<T?> {
            return@Callable getBox().query()
                .equal(getUniqueProperty(), unique, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
                .use { it.findFirst() }
        }) { result, error ->
            error?.printStackTrace()
            Log.i(TAG, "getByUnique unique=$unique result=${result.toString()} , error=$error")
            ThreadUtils.runOnUiThread { queryResult.invoke(result) }
        }
    }

    fun getAll(queryResult: ((data: List<T>?) -> Unit)) {
        DBManager.getDatabase().callInTxAsync(Callable<List<T>> { return@Callable getBox().all }) { result, error ->
            error?.printStackTrace()
            Log.i(TAG, "getAll result=${result?.size} , error=$error")
            ThreadUtils.runOnUiThread { queryResult.invoke(result) }
        }
    }
}