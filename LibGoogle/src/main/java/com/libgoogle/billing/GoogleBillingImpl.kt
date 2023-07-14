package com.libgoogle.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import java.util.concurrent.ConcurrentHashMap

object GoogleBillingImpl {

    const val OK = BillingClient.BillingResponseCode.OK
    const val USER_CANCELED = BillingClient.BillingResponseCode.USER_CANCELED
    const val IN_APP = BillingClient.ProductType.INAPP          //一次性消费
    const val SUBS = BillingClient.ProductType.SUBS             //订阅消费
    private const val PURCHASED = Purchase.PurchaseState.PURCHASED

    const val ON_DISCONNECT = 2001          //谷歌服务断开
    private const val PRODUCT_NOT_FOUND = 2002      //商品未查询到
    private const val CONSUME_FAIL = 2003           //消费失败
    private const val BILL_CLIENT_BE_NULL = 2004    //支付billingClient为null


    private var billingClient: BillingClient? = null

    private val productList = ConcurrentHashMap<String, ProductDetails>()

    private var purchasesCallback: ((code: Int, msg: String?, listPurchase: List<CopyPurchase>?) -> Unit)? = null

    /**
     * google支付回掉
     */
    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        purchasesCallback?.invoke(result.responseCode, result.debugMessage, purchases?.map {
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
    }

    /**
     * 初始化google支付回调
     */
    fun init(context: Context, purchasesCallback: (code: Int, msg: String?, listPurchase: List<CopyPurchase>?) -> Unit) {
        this.purchasesCallback = purchasesCallback

        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    /**
     * 关闭gp服务链接
     */
    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
        purchasesCallback = null
    }

    /**
     * 连接谷歌服务
     */
    fun startConnection(callback: (code: Int, msg: String?) -> Unit) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                callback.invoke(ON_DISCONNECT, "Google Service onDisconnected")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                callback.invoke(billingResult.responseCode, billingResult.debugMessage)
            }
        }) ?: run {
            callback.invoke(BILL_CLIENT_BE_NULL, "startConnection")
        }
    }

    /**
     * 查询未完成的支付
     */
    fun queryPurchasesAsync(callback: (code: Int, msg: String?, listPurchase: List<CopyPurchase>?) -> Unit, subCallback: (code: Int, msg: String?, listPurchase: List<CopyPurchase>?) -> Unit) {
        //查询一次性消费
        realQueryPurchasesAsync(IN_APP, callback)

        //查询订阅
        realQueryPurchasesAsync(SUBS, subCallback)
    }

    private fun realQueryPurchasesAsync(type: String, callback: (code: Int, msg: String?, listPurchase: List<CopyPurchase>?) -> Unit) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(type)
            .build()
        billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
            callback.invoke(billingResult.responseCode, billingResult.debugMessage, purchases.map {
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
            })
        } ?: run {
            callback.invoke(BILL_CLIENT_BE_NULL, "realQueryPurchasesAsync $type", null)
        }
    }

    /**
     * 查询商品信息
     */
    fun queryProductDetailsAsync(productId: String, type: String, callback: (code: Int, msg: String?, size: Int) -> Unit) {
        //从缓存中获取，同一个商品避免每次都去查询商品数据
        getProductDetails(productId)?.let {
            callback.invoke(OK, "", 1)
            return
        }

        //创建查询商品
        val list = listOf(
            QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId(productId)
                .setProductType(type)
                .build()
        )

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(list)
            .build()

        //异步查询商品信息
        billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == OK && productDetailsList.isNotEmpty()) {
                //保存查询过的商品的productId 的sku数据 因为每次都是只查一个，返回的列表也只会是一个
                productDetailsList.forEach { productList[it.productId] = it }
            }
            callback.invoke(billingResult.responseCode, billingResult.debugMessage, productDetailsList.size)
        } ?: run {
            callback.invoke(BILL_CLIENT_BE_NULL, "queryProductDetailsAsync", 0)
        }
    }

    /**
     * 拉起购买
     */
    fun launchPurchase(activity: Activity, obfuscatedAccountId: String, orderId: String, productId: String, callback: (code: Int, msg: String?) -> Unit) {
        val productDetails = getProductDetails(productId) ?: let {
            callback.invoke(PRODUCT_NOT_FOUND, "product not found")
            return
        }

        val offerToken = productDetails.subscriptionOfferDetails?.takeIf { it.size > 0 }?.get(0)?.offerToken

        val list = listOf(
            BillingFlowParams.ProductDetailsParams
                .newBuilder()
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
            callback.invoke(it.responseCode, it.debugMessage)
        } ?: run {
            callback.invoke(BILL_CLIENT_BE_NULL, "launchPurchase")
        }
    }

    /**
     * 向谷歌支付确认消费订单
     */
    fun consumePurchase(purchase: CopyPurchase, callback: (code: Int, msg: String?, token: String) -> Unit) {
        if (purchase.purchaseState == PURCHASED) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient?.consumeAsync(consumeParams) { billingResult, token ->
                callback.invoke(billingResult.responseCode, billingResult.debugMessage, token)
            } ?: run {
                callback.invoke(BILL_CLIENT_BE_NULL, "consumePurchase", "")
            }
        } else {
            callback.invoke(CONSUME_FAIL, "state=${purchase.purchaseState}", "")
        }
    }

    /**
     * 向google确认订阅消费
     */
    fun consumePurchaseSub(purchase: CopyPurchase, callback: (code: Int, msg: String?, token: String) -> Unit) {
        if (purchase.purchaseState == PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) {
                callback.invoke(it.responseCode, it.debugMessage, purchase.purchaseToken)
            } ?: run {
                callback.invoke(BILL_CLIENT_BE_NULL, "consumePurchaseSub", "")
            }
        } else {
            callback.invoke(CONSUME_FAIL, "state=${purchase.purchaseState}  isAcknowledged=${purchase.isAcknowledged}", "")
        }
    }

    /**
     * 本地查询商品信息
     */
    private fun getProductDetails(productId: String): ProductDetails? {
        return productList[productId]
    }

    /**
     * google服务是否已连接
     */
    fun isReady(): Boolean = billingClient?.isReady == true

    /**
     * 是否已经初始化过
     */
    fun isAlreadyInit(): Boolean = billingClient != null

}