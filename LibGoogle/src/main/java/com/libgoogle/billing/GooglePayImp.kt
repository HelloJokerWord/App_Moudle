package com.libgoogle.billing


/**
 * Created on 2022/8/4.
 * @author Joker
 * Des: google支付回调
 */

interface AcknowledgeListener {
    fun result(code: Int, msg: String?, purchaseToken: String)
}

interface BillingClientStateCallback {
    fun onBillingServiceDisconnected()
    fun onBillingSetupFinished(code: Int, msg: String)
}

interface ConsumeResCallback {
    fun onConsumeResponse(code:Int, msg:String, purchaseToken:String)
}

interface LaunchBillingCallback {
    fun onNoProductInfo()
    fun onLaunchResult(code: Int, msg: String)
}

interface PurchasesResCallback {
    fun onQueryPurchasesResponse(code: Int, msg: String, purchases: List<CopyPurchase>)
}

interface PurchasesUpdatedCallback {
    fun onPurchasesUpdated(code: Int, msg: String, purchases: List<CopyPurchase>?)
}

interface ProductDetailsCallback {
    fun onProductDetailsResponse(code: Int, msg: String, productSize: Int)
}
