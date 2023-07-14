package com.libgoogle.billing

import androidx.annotation.Keep

@Keep
data class CopyPurchase(
    val gpOrderId: String,
    val purchaseToken: String,
    val purchaseState: Int,
    val originalJson: String,
    val signature: String,
    val skus: List<String>,
    val financeOrderId: String?,  //金融订单order
    val isAcknowledged: Boolean,
    var payType: String = GoogleBillingImpl.IN_APP,
)