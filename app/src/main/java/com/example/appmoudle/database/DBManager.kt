package com.example.appmoudle.database

import android.util.Log
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.config.GlobalPath
import com.example.appmoudle.database.base.BaseDBEntity
import com.example.appmoudle.database.entity.MyObjectBox
import com.example.appmoudle.manager.LogSwitchManager
import io.objectbox.BoxStore
import io.objectbox.android.Admin
import io.objectbox.exception.FileCorruptException
import io.objectbox.kotlin.boxFor
import java.io.File
import kotlin.reflect.KClass

/**
 * 数据库文件管理类
 */
object DBManager {

    private const val TAG = "DBManager"
    private var mBoxStore: BoxStore? = null

    /**
     * 初始化数据库
     */
    fun init() {
        destroyDatabase()
        createDatabase()
    }

    /**
     * 退出登录后的回调，关闭当前用户的数据库
     */
    fun onAfterLogout() {

    }

    /**
     * 数据库建表
     */
    fun <T : BaseDBEntity> createBox(clazz: KClass<T>) = getDatabase().boxFor(clazz)

    @Synchronized
    fun getDatabase(): BoxStore {
        return mBoxStore ?: createDatabase()
    }

    @Synchronized
    private fun createDatabase(): BoxStore {
        return MyObjectBox.builder()
            .androidContext(Utils.getApp())
            .directory(File(GlobalPath.createMainDBPath()))
            .maxSizeInKByte(1024 * 1024 * 5L) //数据库大小扩增为5G
            .let { objectBoxBuilder ->
                try {
                    objectBoxBuilder.build()
                } catch (e: FileCorruptException) {
                    e.printStackTrace()
                    Log.e(TAG, "objectBox init :File corrupt, trying previous data snapshot...")
                    objectBoxBuilder.usePreviousCommit().build()
                }
            }.also {
                mBoxStore = it

                Log.i(TAG, "Using ObjectBox ${BoxStore.getVersion()}(${BoxStore.getVersionNative()})")
                //是否开启可浏览器查看数据库
                if (LogSwitchManager.isLogEnable()) {
                    val started = Admin(it).start(Utils.getApp())
                    Log.i(TAG, "started + $started")
                }
            }
    }

    @Synchronized
    private fun destroyDatabase() {
        try {
            mBoxStore?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mBoxStore = null
    }

}