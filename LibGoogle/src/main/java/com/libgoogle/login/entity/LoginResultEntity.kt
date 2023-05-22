package com.libgoogle.login.entity

import androidx.annotation.Keep

/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:
 */

@Keep
data class LoginResultEntity(
    val resultCode: Int,
    val resultMsg: String?,
    val loginType: Int,
    val token: String? = null,
) {
    companion object {
        const val CODE_CANCEL = 1
        const val CODE_ERROR = -1
        const val CODE_SUCCESS = 0

        const val LOGIN_TYPE_FACEBOOK = 1
        const val LOGIN_TYPE_GOOGLE = 2
        const val LOGIN_TYPE_PHONE = 3
    }
}
