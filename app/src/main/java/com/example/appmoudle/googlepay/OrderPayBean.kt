package com.example.appmoudle.googlepay

import androidx.annotation.Keep

/**
 * 商品数据
 */
@Keep
data class GoodsEntity(
    val product_id: String?,
    val pay_type: Int,
) {
    companion object {
        const val PAY_TYPE_IN_APP = 0
        const val PAY_TYPE_SUBS = 1
    }
}

/**
 * 创建订单响应
 */
@Keep
data class OrderPayBean(
    val user_id: Long,
    val order_id: String?,
    val product_id: String?,
)

/**
 * 确认订单响应
 */
@Keep
data class PaymentBean(
    val order_id: String?,
    val pay_items_order_id: String?,
    val product_id: String?,
)
