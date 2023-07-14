package com.libhttp

/**
 * CreateBy:Joker
 * CreateTime:2023/5/4 17:34
 * description：
 */
interface RequestCallBack<T> {
    fun onSuccess(data: T)
    fun onFail(code: Int, msg: String?)
}

interface ProgressListener {
    fun onProgress(currentProgress: Int, currentSize: Long, totalSize: Long)
}

interface GetUserParamCallback {
    fun getUserId(): Long

    fun getUserToken(): String?

    fun getGuestId(): String?
}