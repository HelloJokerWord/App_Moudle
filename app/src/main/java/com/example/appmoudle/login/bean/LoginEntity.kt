package com.example.appmoudle.login.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * CreateBy:Joker
 * CreateTime:2023/5/5 10:28
 * description：
 */
@Parcelize
@Keep
data class LoginEntity(
    val uid: Long,
    val access_token: String?,
) : Parcelable
