package com.third.libcommon.http

import androidx.annotation.Keep

/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:
 */

@Keep
data class ResponseBean<T>(
    val code: Int,
    val errorMsg: String?,
    val data: T? = null
)
