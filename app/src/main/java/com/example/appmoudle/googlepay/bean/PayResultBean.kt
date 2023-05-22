package com.example.appmoudle.googlepay.bean

import androidx.annotation.Keep

/**
 * Created on 2022/8/22.
 * @author Joker
 * Des:h5通知
 */
@Keep
data class PayResultBean(
    val payType: String,     //支付类型
    val success: Boolean,
    val orderId: String?,
    val purchaseData: String?,
    val purchaseSign: String?,
)  {
    companion object {
        const val TYPE_GOOGlE_PAY = "googlePay"
    }
}
