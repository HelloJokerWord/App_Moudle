package com.third.libcommon.mmkv

import android.content.Context
import android.os.Parcelable
import android.util.Log

import com.tencent.mmkv.MMKV

object MMKVManager {
    private const val TAG = "MMKVManager"

    private val mkv: MMKV by lazy { MMKV.defaultMMKV() }

    fun init(applicationContext: Context) {
        MMKV.initialize(applicationContext)
    }

    /**
     * 保存
     */
    fun put(key: String, value: Any) {
        when (value) {
            is Boolean -> mkv.encode(key, value)
            is Int -> mkv.encode(key, value)
            is Long -> mkv.encode(key, value)
            is Float -> mkv.encode(key, value)
            is Double -> mkv.encode(key, value)
            is String -> mkv.encode(key, value)
            is ByteArray -> mkv.encode(key, value)
            is Parcelable -> mkv.encode(key, value)
            else -> Log.e(TAG, "unsupported type of value")
        }
    }

    /**
     * 获取
     */
    fun getBoolean(key: String, default: Boolean = false) = mkv.decodeBool(key, default)
    fun getInt(key: String, default: Int = 0) = mkv.decodeInt(key, default)
    fun getLong(key: String, default: Long = 0L) = mkv.decodeLong(key, default)
    fun getFloat(key: String, default: Float = 0f) = mkv.decodeFloat(key, default)
    fun getDouble(key: String, default: Double = 0.0) = mkv.decodeDouble(key, default)
    fun getString(key: String, default: String = "") = mkv.decodeString(key, default) ?: default
    fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>, default: T? = null) = mkv.decodeParcelable(key, clazz, default) ?: default

    /**
     * 是否存在该key
     */
    fun isContain(key: String) = mkv.contains(key)

    /**
     * 数据全清除
     */
    fun clear() {
        mkv.clearAll()
    }

    /**
     * 删除某个数据
     */
    fun remove(key: String) {
        mkv.remove(key)
    }

}