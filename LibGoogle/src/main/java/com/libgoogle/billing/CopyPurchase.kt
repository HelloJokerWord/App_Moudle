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
    var useToken: Int = 0,        //1：需要服务端去尝试消费 0：不用，直接发货
    var payType: String = GoogleBillingImpl.IN_APP,
)