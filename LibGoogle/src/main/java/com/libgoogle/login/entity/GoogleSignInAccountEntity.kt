package com.libgoogle.login.entity

import android.accounts.Account
import android.net.Uri
import androidx.annotation.Keep

/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:
 */
@Keep
data class GoogleSignInAccountEntity(
    val account: Account?,
    val photoUrl: Uri?,
    val displayName: String?,
    val email: String?,
    val familyName: String?,
    val givenName: String?,
    val id: String?,
    val idToken: String?,
    val serverAuthCode: String?
)
