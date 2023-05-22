package com.libgoogle.appsfly

import androidx.annotation.Keep

/**
 * Created on 2022/8/15.
 * @author Joker
 * Des:
 */

@Keep
data class InstallReferrerResult(
    val code: Int,
    val msg: String?,
    val referrerUrl: String? = "",
    val referrerClickTime: Long = 0L,
    val appInstallTime: Long = 0L,
    val instantExperienceLaunched: Boolean = false,
){
    override fun toString(): String {
        return "InstallReferrerResult(code=$code, msg=$msg, referrerUrl=$referrerUrl, referrerClickTime=$referrerClickTime, appInstallTime=$appInstallTime, instantExperienceLaunched=$instantExperienceLaunched)"
    }
}
