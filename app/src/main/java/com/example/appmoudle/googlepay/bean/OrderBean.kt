package com.example.appmoudle.googlepay.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * 创建订单响应
 */
@Keep
data class OrderBean(
    val app_account_token: String?,
    val country_code: String?,
    val is_first_charge: Boolean,
    val money: String?,
    val num: Int,
    val order_id: String?,
    val paycode: String?,
    val price: String?,
    val product_id: String?,
    val paypal_href: String?,
    val currency: String?,
)

/**
 * 确认订单响应
 */
@Keep
data class PaymentBean(
    val uid: Long,
    val money: String?,
    val currency: String?,
    val order_id: String?,
    val product_id: String?,
    val out_trade_no: String?
)

/**
 * 请求余额响应
 */
@Parcelize
@Keep
data class BalanceBean(
    val accounts: MutableMap<String, Account>?,   //0 代表金币 2 代表钻石
    val uid: Long
) : Parcelable

@Parcelize
@Keep
data class Account(
    val account_type: Int,
    val quantity: Double
) : Parcelable