package com.example.appmoudle.googlepay

import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.R
import com.example.appmoudle.agentweb.AgentWebBuilder
import com.example.appmoudle.agentweb.CallBackJsBean
import com.example.appmoudle.agentweb.JsCMD
import com.example.appmoudle.config.GlobalUserManager
import com.example.appmoudle.googlepay.bean.OrderBean
import com.example.appmoudle.googlepay.bean.PayResultBean
import com.example.appmoudle.googlepay.bean.PaymentBean
import com.libgoogle.appsfly.AppsFlyerManager
import com.libgoogle.billing.*
import com.third.libcommon.WeakHandler
import com.third.libcommon.extension.fromJson
import com.third.libcommon.extension.genericType
import com.third.libcommon.extension.toJson
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack
import com.third.libcommon.mmkv.MMKVKey
import com.third.libcommon.mmkv.MMKVManager
import org.json.JSONObject
import java.lang.ref.WeakReference


/**
 * 支付管理类
 */
object GooglePayManager {
    private const val TAG = "GooglePayManager"

    private const val PAY_ERROR_GMS_UNAVAILABLE = "1001"           //无gms服务
    private const val PAY_ERROR_RECONNECT_SERVICE = "1002"         //服务重连
    private const val PAY_ERROR_NO_CHARGE_ID = "1003"              //无订单ID
    private const val PAY_ERROR_GMS_DISCONNECT = "1004"            //google支付服务断开
    private const val PAY_ERROR_NO_LOGIN = "1005"                  //未登陆
    private const val PAY_ERROR_CREATE_ORDER_FAIL = "1006"         //创建订单失败
    private const val PAY_ERROR_NO_SKU_INFO = "1009"               //无SKU信息
    private const val PAY_ERROR_INVALID_ORDER = "1010"             //无效订单，不可消费

    private const val CONSUME_FROM_PAY = "pay"
    private const val CONSUME_FROM_CHECK = "check"

    private const val GOOGLE_PAY_MANNER_ID = 31  //google支付类型

    private var weakActivity: WeakReference<FragmentActivity>? = null
    private val weakHandler = WeakHandler()

    @Volatile
    private var reConnectCount = 5               //重连次数

    private var lastDoPayTime = 0L              //上次调起支付

    private var isFirstCharge = false
    private var payType = GoogleBillingImpl.IN_APP
    private var financeOrderId: String? = null
    private var chargeSource: String? = null   //启动支付场景来源
    private var chargeId: Int = 0              //支付挡位id
    private var tracePurchase: CopyPurchase? = null

    var mAgentWebBuilder: AgentWebBuilder? = null

    /**
     * 1、初始化google支付环境
     */
    fun init() {
        GoogleBillingImpl.init(Utils.getApp(), object : PurchasesUpdatedCallback {
            override fun onPurchasesUpdated(code: Int, msg: String, purchases: List<CopyPurchase>?) {
                //[CopyPurchase(
                // gpOrderId=GPA.3388-4266-9876-00111,
                // purchaseToken=agbmahkppkoapjfjncplogfo.AO-J1Ozli4QkyoNr6h53QDmAaazfOVoK5ioh0xwT3AzDG3Q8TXLMJZnn1PzIiG-zJZJWD0dTeZKn2Fcq4DdO15_vZpRDsZRw4g,
                // purchaseState=1,
                // originalJson={
                // "orderId":"GPA.3388-4266-9876-00111",
                // "packageName":"com.hpc.happy.chat",
                // "productId":"happychat0.99",
                // "purchaseTime":1669132884297,
                // "purchaseState":0,
                // "purchaseToken":"agbmahkppkoapjfjncplogfo.AO-J1Ozli4QkyoNr6h53QDmAaazfOVoK5ioh0xwT3AzDG3Q8TXLMJZnn1PzIiG-zJZJWD0dTeZKn2Fcq4DdO15_vZpRDsZRw4g",
                // "obfuscatedAccountId":"10000",
                // "obfuscatedProfileId":"GK2328082055118641275992027",
                // "quantity":1,
                // "acknowledged":false},
                // signature=FnxmgDRa8429VNmh28B9NpZwwVbHVQQ9CKIbsZ2GJ7cbGCCpQrnr+8PDBmDZaOmhtkVAEuNWYvGf9PYY4hlvRGEj9PGRYrG+FggXWWC9Lg4XSX4BrBrZz1LX1UvRiuka2CzVAGmKGUICpwmFTAuOZw11fZ8RAcnX7O6ZIdgPqprYsOvR9AogBTO7ZUYzESB1cYDSXpp2VY21Uar0iNTXjkzPQxfWA3C/4UuIvK5vCZMqZyU+QispgrpsxYFnyY1jR1LZa2Ru/PJbQb55jtLPfahXZ2BeIeFmiKW8C+04rzqwtiK6ZnaHh+WZoOEGDgYvFYIr2ge82Gv6J2BwTsW5sA==,
                // skus=[happychat0.99],
                // financeOrderId=GK2328082055118641275992027,
                // isAcknowledged=false)]
                when (code) {
                    GoogleBillingImpl.OK -> consumePurchaseList(payType, purchases, CONSUME_FROM_PAY)
                    GoogleBillingImpl.USER_CANCELED -> {
                        ToastUtils.showLong(R.string.hc_word_cancel)
                        trackAndLogPay(step = "支付回调：取消支付", error_code = code.toString(), error_msg = msg, isShowToast = false)
                    }

                    else -> trackAndLogPay(step = "支付回调：支付失败", error_code = code.toString(), error_msg = msg)
                }
            }
        })
        connectGP()
    }

    /**
     * 退出应用
     */
    fun exitApp() {
        GoogleBillingImpl.exitApp()
    }

    /**
     * 退出支付页面
     */
    fun onDestroy() {
        mAgentWebBuilder = null
        weakActivity = null
    }

    /**
     * 1.1查询给用户是否有缓存未处理订单
     */
    fun checkOrderCache() {
        Log.i(TAG, "开始查询补单  uid=${getMyUid()} isConnected=${GoogleBillingImpl.isReady()}")
        if (getMyUid() > 0L) {
            if (GoogleBillingImpl.isReady()) {
                queryPurchases()
                queryNotConfirm()
            } else {
                reConnectCount = 5
                retryConnect(true)
            }
        }
    }

    /**
     * 2.连接google服务
     */
    private fun connectGP() {
        if (!isGpAvailable()) {
            val availableCode = GooglePayAuthImpl.googleServiceAvailableCode(Utils.getApp())
            val msg = GooglePayAuthImpl.googleServiceAvailableMsg(availableCode)
            trackAndLogPay(step = "连接Google服务_1.0", error_code = availableCode.toString(), error_msg = msg)
            return
        }

        if (GoogleBillingImpl.isReady()) {
            Log.e(TAG, "google服务已连接初始化过了")
            return
        }

        GoogleBillingImpl.startConnection(object : BillingClientStateCallback {
            override fun onBillingServiceDisconnected() {
                retryConnect()
                trackAndLogPay(step = "连接Google服务_1.1", error_code = PAY_ERROR_GMS_DISCONNECT, error_msg = "google服务链接已断开", isShowToast = false)
            }

            override fun onBillingSetupFinished(code: Int, msg: String) {
                if (code == GoogleBillingImpl.OK) {
                    Log.i(TAG, "Google服务连接回调  code=$code,msg=$msg")
                    reConnectCount = 5
                    //查询支付未完成缓存，补单
                    checkOrderCache()
                } else {
                    retryConnect()
                    trackAndLogPay(step = "连接Google服务_1.2", error_code = code.toString(), error_msg = msg, isShowToast = false)
                }
            }
        })
    }

    /**
     * 2.1尝试重连
     */
    private fun retryConnect(isImmediately: Boolean = false) {
        if (reConnectCount > 0 || isImmediately) {
            weakHandler.removeCallbacksAndMessages(null)
            weakHandler.postDelayed({
                reConnectCount--
                Log.e(TAG, "谷歌服务重连")
                trackAndLogPay(step = "retryConnect_1.0", error_code = PAY_ERROR_RECONNECT_SERVICE, error_msg = "谷歌服务重连 $reConnectCount", isShowToast = false)
                connectGP()
            }, if (isImmediately) 10 else 5000L)
        }
    }

    /**
     *  3.根据商品id查询支付档位信息
     */
    private fun queryProductDetail(activity: FragmentActivity, bean: OrderBean) {
        if (!isGpAvailable() || !GoogleBillingImpl.isReady()) {
            trackAndLogPay(step = "pay_1.0", error_code = PAY_ERROR_GMS_UNAVAILABLE, error_msg = "Google服务不可用")
            retryConnect()
            return
        }

        weakActivity = WeakReference(activity)

        Log.i(TAG, "开始查询支付档位信息")
        GoogleBillingImpl.queryProductDetailsAsync("${bean.product_id}", payType, object : ProductDetailsCallback {
            override fun onProductDetailsResponse(code: Int, msg: String, productSize: Int) {
                if (code == GoogleBillingImpl.OK && productSize > 0) {
                    Log.i(TAG, "支付档位查询结果 size=$productSize")
                    weakActivity?.get()?.let {
                        //TraceManager.traceAndroidError("GooglePayManager", "queryProductDetail", "type=$payType", "financeOrderId=$financeOrderId")
                        launchPay(it, bean)
                    } ?: run {
                        trackAndLogPay(step = "支付档位查询_1.1", error_code = code.toString(), error_msg = "weakActivity=null", isShowToast = false)
                    }
                } else {
                    trackAndLogPay(step = "支付档位查询_1.0", error_code = code.toString(), error_msg = msg)
                }
            }
        })
    }

    /**
     * 4.调起谷歌支付界面
     */
    private fun launchPay(activity: Activity, bean: OrderBean) {
        //(orderId='GK2328082055118641275992027', productId='happychat0.99', obfuscatedAccountId='10000', price='99', currency='USD', productType='inapp')
        Log.i(TAG, "开始拉起Google支付弹窗")
        GoogleBillingImpl.launchPurchase(activity, getMyUid().toString(), "${bean.order_id}", "${bean.product_id}", object : LaunchBillingCallback {
            override fun onNoProductInfo() {
                trackAndLogPay(step = "launchPay_1.0", error_code = PAY_ERROR_NO_SKU_INFO, error_msg = "未查询到有效sku信息")
            }

            override fun onLaunchResult(code: Int, msg: String) {
                if (code == GoogleBillingImpl.OK) {
                    Log.w(TAG, "拉起支付弹窗成功")
                    //TraceManager.traceAndroidError("GooglePayManager", "launchPay", "type=$payType", "financeOrderId=$financeOrderId")
                } else {
                    trackAndLogPay(step = "launchPay_1.2", error_code = code.toString(), error_msg = msg)
                }
            }
        })
    }

    /**
     *  5.筛选能处理的订单
     */
    private fun consumePurchaseList(payType: String, purchases: List<CopyPurchase>?, from: String) {
        purchases.takeIf { !it.isNullOrEmpty() }?.forEach { purchase ->
            Log.i(TAG, "筛选能消费的订单 type=$payType purchase=$purchase")
            tracePurchase = purchase
            financeOrderId = purchase.financeOrderId

            if (purchase.purchaseState == GoogleBillingImpl.PURCHASED) {
                //TraceManager.traceAndroidError("GooglePayManager", "consumePurchaseList", "type=$payType", "purchase=$purchase")
                when (payType) {
                    GoogleBillingImpl.IN_APP -> {  //一次性消费
                        consumePurchase(purchase, from)
                    }

                    GoogleBillingImpl.SUBS -> {   //订阅消费
                        if (!purchase.isAcknowledged) {
                            consumePurchaseSub(purchase, from)
                        } else {
                            Log.e(TAG, "已订阅购买过，code:${purchase.gpOrderId}}")
                        }
                    }
                }
            } else {
                trackAndLogPay(step = "consumePurchaseList_1.0 $from", error_code = PAY_ERROR_INVALID_ORDER, error_msg = "不可消费订单", isShowToast = false)

                //处理掉单
                purchase.payType = payType
                purchase.useToken = 1
                reqConfirmOrder(purchase, from)
            }
        }
    }

    /**
     * 6.订阅商品确认购买交易
     */
    private fun consumePurchaseSub(purchase: CopyPurchase, from: String) {
        Log.i(TAG, "consumePurchaseSub 开始消耗订阅项目")
        GoogleBillingImpl.consumePurchaseSub(purchase, object : AcknowledgeListener {
            override fun result(code: Int, msg: String?, purchaseToken: String) {
                consumeResult(code, msg, purchase, GoogleBillingImpl.SUBS, from)
            }
        })
    }

    /**
     * 6.1消耗已购买的项目
     */
    private fun consumePurchase(purchase: CopyPurchase, from: String) {
        Log.i(TAG, "开始消耗商品")
        GoogleBillingImpl.consumePurchase(purchase.purchaseToken, object : ConsumeResCallback {
            override fun onConsumeResponse(code: Int, msg: String, purchaseToken: String) {
                //TraceManager.traceAndroidError("GooglePayManager", "consumePurchase", "type=$payType", "purchase=$purchase")
                consumeResult(code, msg, purchase, GoogleBillingImpl.IN_APP, from)
            }
        })
    }

    /**
     * 7.处理消费结果
     */
    private fun consumeResult(code: Int, msg: String?, purchase: CopyPurchase, payType: String, from: String) {
        if (code != GoogleBillingImpl.OK) {
            trackAndLogPay(step = "处理消费结果 type=$payType", error_code = code.toString(), error_msg = msg, isShowToast = false)
        }

        //消费结果，请求确认订单, 掉单的:useToken=1
        purchase.payType = payType
        purchase.useToken = if (code == GoogleBillingImpl.OK) 0 else 1
        reqConfirmOrder(purchase, from)
    }

    /**
     * 1.2查询历史订单是否有未被消耗的
     */
    private fun queryPurchases() {
        if (getMyUid() < 0) {
            trackAndLogPay(step = "queryPurchases_1.0", error_code = PAY_ERROR_NO_LOGIN, error_msg = "未登录，暂不查询交易缓存", isShowToast = false)
            return
        }

        Log.i(TAG, "开始查询交易缓存")
        GoogleBillingImpl.queryPurchasesAsync(object : PurchasesResCallback {
            override fun onQueryPurchasesResponse(code: Int, msg: String, purchases: List<CopyPurchase>) {
                checkConsume(GoogleBillingImpl.IN_APP, code, msg, purchases)
            }
        }, object : PurchasesResCallback {
            //查询消费订单
            override fun onQueryPurchasesResponse(code: Int, msg: String, purchases: List<CopyPurchase>) {
                checkConsume(GoogleBillingImpl.SUBS, code, msg, purchases)
            }
        })
    }

    /**
     * 1.3检查并消费
     */
    private fun checkConsume(payType: String, code: Int, msg: String, purchases: List<CopyPurchase>) {
        Log.i(TAG, "查询到的交易缓存数量 type=$payType code=$code msg=$msg purchases=$purchases")
        if (code == GoogleBillingImpl.OK) {
            consumePurchaseList(payType, purchases, CONSUME_FROM_CHECK)
        } else {
            //查询交易缓存失败
            trackAndLogPay(step = "queryPurchases_1.1", error_code = code.toString(), error_msg = msg, isShowToast = false)
        }
    }

    /**
     * isGooglePlayServicesAvailable方法说明：
     * https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#isGooglePlayServicesAvailable(android.content.Context)
     */
    private fun isGpAvailable() = GooglePayAuthImpl.googleServiceAvailableCode(Utils.getApp()) == GooglePayAuthImpl.AVAILABLE_CODE_SUCCESS

    /**
     * 支付错误或中断埋点上报
     * @param step          支付步骤节点
     * @param result        支付结果
     * @param error_code    错误码
     * @param error_msg     错误信息
     */
    private fun trackAndLogPay(step: String, result: Int = 0, error_code: String, error_msg: String?, isShowToast: Boolean = true) {
        if (result == 0) {
            Log.e(TAG, "$step error_code=$error_code error_msg=$error_msg orderId=$financeOrderId")
            if (isShowToast) ToastUtils.showLong(R.string.hc_toast_try_again, error_code)
            notifyWeb(PayResultBean(PayResultBean.TYPE_GOOGlE_PAY, false, financeOrderId, "", ""))
        }

        //TraceManager.traceGooglePay(chargeSource, step, chargeId, result, if (isFirstCharge) 1 else 0, financeOrderId, error_code, "error_msg=${error_msg}  purchase=$tracePurchase")
    }

    /**
     * 1.1创建订单支付
     */
    fun createOrder(activity: FragmentActivity, data: JSONObject?) {
        //防抖动点击重复拉起支付
        val curTime = System.currentTimeMillis()
        if (curTime - lastDoPayTime < 2000L) return
        lastDoPayTime = curTime

        if (!GoogleBillingImpl.isReady()) {
            trackAndLogPay(step = "订单无法创建_1", error_code = PAY_ERROR_GMS_UNAVAILABLE, error_msg = "Google服务未连接", isShowToast = reConnectCount <= 3)
            retryConnect(true)
            return
        }

        chargeId = data?.optInt("id", -1) ?: -1
        Log.i(TAG, "开始创建订单 charge_id=$chargeId")
        if (chargeId < 0) {
            trackAndLogPay(step = "订单无法创建_2", error_code = PAY_ERROR_NO_CHARGE_ID, error_msg = "No charge_id")
            return
        }

        val localPayType = data?.optInt("type", -1) ?: -1
        chargeSource = data?.optString("source")
        payType = if (localPayType == 1) GoogleBillingImpl.SUBS else GoogleBillingImpl.IN_APP

        //请求获取订单
        val params = mutableMapOf<String, Any?>()
        params["charge_id"] = chargeId
        params["manner_id"] = GOOGLE_PAY_MANNER_ID

        HttpManager.postBody(activity, "", params, object : RequestCallBack<OrderBean?> {
            override fun onSuccess(data: OrderBean?) {
                if (data == null) {
                    trackAndLogPay(step = "订单无法创建_3", error_code = PAY_ERROR_CREATE_ORDER_FAIL, error_msg = "创建订单无数据返回")
                    return
                }
                //(app_account_token=, country_code=CN, is_first_charge=false, money=0.99, num=777, order_id=GK2228082055119638936485373, paycode=, price=99, product_id=happychat0.99, paypal_href=null, currency=USD)
                Log.i(TAG, "订单创建成功 $data")
                isFirstCharge = data.is_first_charge
                financeOrderId = data.order_id
                //TraceManager.traceAndroidError("GooglePayManager", "createOrder", "type=$payType", "financeOrderId=$financeOrderId")

                queryProductDetail(activity, data)
            }

            override fun onFail(code: Int, msg: String?) {
                trackAndLogPay(step = "订单无法创建_4", error_code = code.toString(), error_msg = msg)
            }

        })
    }

    /**
     * 8.请求确认订单
     * @param purchase 支付成功才会返回
     */
    private fun reqConfirmOrder(purchase: CopyPurchase, from: String) {
        saveConfirmOrder(purchase)

        val params = mutableMapOf<String, Any?>()
        params["inapp_purchase_data"] = purchase.originalJson
        params["inapp_purchase_signature"] = purchase.signature
        params["manner_id"] = GOOGLE_PAY_MANNER_ID
        params["order_id"] = purchase.financeOrderId
        params["pay_type"] = purchase.payType

        val original = fromJson<GoogleOriginalJsonBean>(purchase.originalJson)
        params["package_name"] = original?.packageName
        params["product_id"] = original?.productId
        params["purchase_token"] = original?.purchaseToken
        params["use_token"] = purchase.useToken

        weakActivity?.get()?.let { activity ->
            HttpManager.postBody(activity, "", params, object : RequestCallBack<PaymentBean?> {
                override fun onSuccess(data: PaymentBean?) {
                    Log.w(TAG, "请求确认完毕，交易结束 orderId=$financeOrderId bean=$data")
                    trackAndLogPay(step = "请求确认完毕，交易结束", result = 1, error_code = "0", error_msg = "交易结束 bean=$data", isShowToast = false)
                    data?.let { AppsFlyerManager.onPaySuccess(Utils.getApp(), "${it.uid}", "${it.money}", "${it.currency}", "${it.out_trade_no}", "${it.order_id}") }

                    removeConfirmOrder(purchase)

                    //通知本地请求刷新余额
                    //LiveEventManager.post(EventReqBalance())
                    //通知web端支付完成
                    notifyWeb(PayResultBean(PayResultBean.TYPE_GOOGlE_PAY, true, financeOrderId, purchase.originalJson, purchase.signature))

                    if (from == CONSUME_FROM_PAY) {
                        ToastUtils.showShort("Recharge Success")
                    }
                }

                override fun onFail(code: Int, msg: String?) {
                    // trackAndLogPay(step = "请求确认订单失败_2", error_code = code.toString(), error_msg = msg, isShowToast = from == CONSUME_FROM_PAY)
                    if (code == 23001 || code == 23099) {    //重复确认订单
                        removeConfirmOrder(purchase)
                    }
                }
            })
        }
    }

    /**
     * 1.1查询未确认完毕的订单
     */
    private fun queryNotConfirm() {
        val noConfirmOrderMap = getOrderMap()
        Log.i(TAG, "本地已支付未确认订单数量：${noConfirmOrderMap.size}")
        if (noConfirmOrderMap.isEmpty()) return
        noConfirmOrderMap.forEach { reqConfirmOrder(it.value, CONSUME_FROM_CHECK) }
    }

    private fun getOrderMap(): MutableMap<String, CopyPurchase> {
        val mapStr = MMKVManager.getString(MMKVKey.NO_CONFIRM_ORDER)
        return if (mapStr.isEmpty()) mutableMapOf() else {
            fromJson<MutableMap<String, CopyPurchase>>(mapStr, genericType<MutableMap<String, CopyPurchase>>()) ?: mutableMapOf()
        }
    }

    private fun saveConfirmOrder(purchase: CopyPurchase) {
        Log.i(TAG, "确认订单前保存 ${purchase.gpOrderId}")
        val orderMap = getOrderMap().apply { put(purchase.gpOrderId, purchase) }
        MMKVManager.put(MMKVKey.NO_CONFIRM_ORDER, orderMap.toJson())
    }

    private fun removeConfirmOrder(purchase: CopyPurchase) {
        val old = getOrderMap()
        if (old.isNotEmpty()) {
            old.remove(purchase.gpOrderId)
            Log.i(TAG, "确认订单后删除 ${purchase.gpOrderId} size=${old.size}")
            //更新删除之后的
            MMKVManager.put(MMKVKey.NO_CONFIRM_ORDER, old.toJson())
        }
    }

    /**
     * 9.通知web端支付完成
     */
    private fun notifyWeb(payResultBean: PayResultBean) {
        mAgentWebBuilder?.javaScriptHandler?.callJs(CallBackJsBean(JsCMD.doPayResult, payResultBean.toJson()).toJson())
    }

    private fun getMyUid() = GlobalUserManager.getUid()
}