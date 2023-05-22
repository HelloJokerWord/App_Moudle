package com.example.appmoudle.googlepay

import androidx.annotation.Keep

@Keep
data class GoogleOriginalJsonBean(
    val acknowledged: Boolean,
    val obfuscatedAccountId: String?,
    val obfuscatedProfileId: String?,
    val orderId: String?,
    val packageName: String?,
    val productId: String?,
    val purchaseState: Int,
    val purchaseTime: Long,
    val purchaseToken: String?,
    val quantity: Int
)