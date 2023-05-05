package com.example.appmoudle.agentweb

import androidx.annotation.Keep

/**
 * Created on 2022/8/25.
 * @author Joker
 * Des:
 */
@Keep
data class CallBackJsBean(
    val action: String,    //方法名字
    val data: String? = "",
)