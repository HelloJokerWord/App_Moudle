package com.libgoogle.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams


/**
 * 只将goggle支付相关初始化隔离
 */
object GoogleBillingImpl {

    private const val TAG = "GpBilling"

    const val OK = BillingClient.BillingResponseCode.OK
    const val USER_CANCELED = BillingClient.BillingResponseCode.USER_CANCELED
    private const val DEVELOPER_ERROR = BillingClient.BillingResponseCode.DEVELOPER_ERROR

    const val IN_APP = BillingClient.ProductType.INAPP          //一次性消费
    const val SUBS = BillingClient.ProductType.SUBS             //订阅消费
    const val PURCHASED = Purchase.PurchaseState.PURCHASED

    private var billingClient: BillingClient? = null
    private var productList = mutableListOf<ProductDetails>()

    fun init(context: Context, callback: PurchasesUpdatedCallback) {
        Log.i(TAG, "GpBilling init")
        billingClient = BillingClient.newBuilder(context).setListener { result, purchases ->
            Log.i(TAG, "GpBilling result=$result purchases=$purchases")
            callback.onPurchasesUpdated(result.responseCode, result.debugMessage, purchases?.map {
                CopyPurchase(
                    it.orderId ?: "",
                    it.purchaseToken,
                    it.purchaseState,
                    it.originalJson,
                    it.signature,
                    it.products,
                    it.accountIdentifiers?.obfuscatedProfileId,
                    it.isAcknowledged,
                )
            })
        }.enablePendingPurchases().build()
    }

    fun exitApp() {
        Log.i(TAG, "GpBilling exitApp")
        billingClient?.endConnection()
        billingClient = null
    }

    /**
     * 连接谷歌支付
     */
    fun startConnection(callback: BillingClientStateCallback) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                callback.onBillingServiceDisconnected()
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                callback.onBillingSetupFinished(billingResult.responseCode, billingResult.debugMessage)
            }
        })
    }

    /**
     * 查询未完成的支付
     */
    fun queryPurchasesAsync(callback: PurchasesResCallback, subCallback: PurchasesResCallback) {
        //查询一次性消费
        realQueryPurchasesAsync(IN_APP, callback)

        //查询订阅
        realQueryPurchasesAsync(SUBS, subCallback)
    }

    private fun realQueryPurchasesAsync(type: String, callback: PurchasesResCallback) {
        val params = QueryPurchasesParams.newBuilder().setProductType(type).build()
        billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
            callback.onQueryPurchasesResponse(
                billingResult.responseCode,
                billingResult.debugMessage,
                purchases.map {
                    CopyPurchase(
                        it.orderId ?: "",
                        it.purchaseToken,
                        it.purchaseState,
                        it.originalJson,
                        it.signature,
                        it.products,
                        it.accountIdentifiers?.obfuscatedProfileId,
                        it.isAcknowledged
                    )
                }
            )
        }
    }

    /**
     * 查询商品信息
     *
     * 1、查询是否获取过该商品数据，如果获取过，从缓存中获取返回
     * 2、没有获取过该商品，调接口获取，并保存
     *
     * 至于为什么一开始没有给到全部的商品一次查询，就不得而知了
     */
    fun queryProductDetailsAsync(productId: String, type: String, callback: ProductDetailsCallback) {

        //从缓存中获取，同一个商品避免每次都去查询商品数据
        getProductDetails(productId)?.let {
            callback.onProductDetailsResponse(OK, "", 1)
            return
        }

        //创建查询商品
        val list = listOf(QueryProductDetailsParams.Product.newBuilder().setProductId(productId).setProductType(type).build())

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(list).build()

        //异步查询商品信息
        billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == OK && productDetailsList.isNotEmpty()) {
                //保存查询过的商品的productId 的sku数据 因为每次都是只查一个，返回的列表也只会是一个
                productList.addAll(productDetailsList)
            }
            callback.onProductDetailsResponse(billingResult.responseCode, billingResult.debugMessage, productDetailsList.size)
        }
    }

    /**
     * 拉起购买
     */
    fun launchPurchase(activity: Activity, obfuscatedAccountId: String, orderId: String, productId: String, callback: LaunchBillingCallback) {
        val productDetails = getProductDetails(productId) ?: let {
            callback.onNoProductInfo()
            return
        }

        Log.i(TAG, "productDetails=$productDetails")
        val offerToken = productDetails.subscriptionOfferDetails?.takeIf { it.size > 0 }?.get(0)?.offerToken

        val list = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken ?: "")
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(list)
            .setObfuscatedAccountId(obfuscatedAccountId)  //金融标识符
            .setObfuscatedProfileId(orderId)              //金融标识符
            .setIsOfferPersonalized(true)                 //欧盟消费者权益指令
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)?.let {
            callback.onLaunchResult(it.responseCode, it.debugMessage)
        } ?: run {
            callback.onLaunchResult(DEVELOPER_ERROR, "billingClient be null")
        }
    }

    /**
     * 向谷歌支付确认消费订单
     */
    fun consumePurchase(purchaseToken: String, callback: ConsumeResCallback) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.consumeAsync(consumeParams) { billingResult, token ->
            callback.onConsumeResponse(billingResult.responseCode, billingResult.debugMessage, token)
        }
    }

    /**
     * 向google确认订阅消费
     */
    fun consumePurchaseSub(purchase: CopyPurchase, acknowledgeListener: AcknowledgeListener) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) {
            acknowledgeListener.result(it.responseCode, it.debugMessage, purchase.purchaseToken)
        }
    }

    /**
     * 本地查询商品信息
     */
    private fun getProductDetails(productId: String): ProductDetails? {
        Log.i(TAG, "总的 productList=${productList.size}")
        return productList.find { it.productId == productId }
    }

    fun isReady() = billingClient?.isReady ?: false

}