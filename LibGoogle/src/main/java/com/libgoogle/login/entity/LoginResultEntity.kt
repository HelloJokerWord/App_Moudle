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
    val third_token: String? = null,
    val third_id: String? = null,
    val thirdCode: String? = null,
    val thirdSecret: String? = null,
    val email: String? = null,
    val loginname: String? = null,
    val avatar: String? = null,
    val password: String? = null,
) {
    companion object {
        const val CODE_CANCEL = 1
        const val CODE_ERROR = -1
        const val CODE_SUCCESS = 0

        const val LOGIN_TYPE_FACEBOOK = 1
        const val LOGIN_TYPE_GOOGLE = 2
        const val LOGIN_TYPE_EMAIL = 3
        const val LOGIN_TYPE_TWITTER = 4
    }
}
