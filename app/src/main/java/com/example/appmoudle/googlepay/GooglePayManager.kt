package com.example.appmoudle.googlepay

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.R
import com.example.appmoudle.global.EventPayResult
import com.example.appmoudle.global.GlobalUserManager
import com.example.appmoudle.main.MainF
import com.libgoogle.billing.CopyPurchase
import com.libgoogle.billing.GoogleBillingImpl
import com.libgoogle.billing.GooglePayAuthImpl
import com.libhttp.HttpManager
import com.libhttp.RequestCallBack
import com.libhttp.URLApi
import com.third.libcommon.LiveEventManager
import com.third.libcommon.WeakHandler
import com.third.libcommon.extension.fromJson
import com.third.libcommon.extension.genericType
import com.third.libcommon.extension.toJson
import com.third.libcommon.mmkv.MMKVKey
import com.third.libcommon.mmkv.MMKVManager


/**
 * 支付管理类
 */
object GooglePayManager {
    private const val TAG = "GooglePayManager"

    private const val PAY_ERROR_CREATE_ORDER_FAIL = "1001"         //创建订单失败

    private const val CONSUME_FROM_PAY = "pay"
    private const val CONSUME_FROM_CHECK = "check"

    private var fragmentActivity: FragmentActivity? = null
    private val weakHandler = WeakHandler()

    @Volatile
    private var reConnectCount = 5               //重连次数

    private var payType = GoogleBillingImpl.IN_APP

    @Volatile
    private var isConnecting = false

    /*
     * 1、初始化google支付环境
     * CopyPurchase(
     * gpOrderId=GPA.3388-4266-9876-00111,
     * purchaseToken=agbmahkppkoapjfjncplogfo.AO-J1Ozli4QkyoNr6h53QDmAaazfOVoK5ioh0xwT3AzDG3Q8TXLMJZnn1PzIiG-zJZJWD0dTeZKn2Fcq4DdO15_vZpRDsZRw4g,
     * purchaseState=1,
     *
     * originalJson={
     * "orderId":"GPA.3388-4266-9876-00111",
     * "packageName":"com.hpc.happy.chat",
     * "productId":"happychat0.99",
     * "purchaseTime":1669132884297,
     * "purchaseState":0,
     * "purchaseToken":"agbmahkppkoapjfjncplogfo.AO-J1Ozli4QkyoNr6h53QDmAaazfOVoK5ioh0xwT3AzDG3Q8TXLMJZnn1PzIiG-zJZJWD0dTeZKn2Fcq4DdO15_vZpRDsZRw4g",
     * "obfuscatedAccountId":"10000",
     * "obfuscatedProfileId":"GK2328082055118641275992027",
     * "quantity":1,
     * "acknowledged":false
     * },
     *
     * signature=FnxmgDRa8429VNmh28B9NpZwwVbHVQQ9CKIbsZ2GJ7cbGCCpQrnr+8PDBmDZaOmhtkVAEuNWYvGf9PYY4hlvRGEj9PGRYrG+FggXWWC9Lg4XSX4BrBrZz1LX1UvRiuka2CzVAGmKGUICpwmFTAuOZw11fZ8RAcnX7O6ZIdgPqprYsOvR9AogBTO7ZUYzESB1cYDSXpp2VY21Uar0iNTXjkzPQxfWA3C/4UuIvK5vCZMqZyU+QispgrpsxYFnyY1jR1LZa2Ru/PJbQb55jtLPfahXZ2BeIeFmiKW8C+04rzqwtiK6ZnaHh+WZoOEGDgYvFYIr2ge82Gv6J2BwTsW5sA==,
     * skus=[happychat0.99],
     * financeOrderId=GK2328082055118641275992027,
     * isAcknowledged=false)]
     */
    fun init(mainF: MainF) {
        if (!GoogleBillingImpl.isAlreadyInit()) {
            GoogleBillingImpl.init(Utils.getApp()) { code, msg, listPurchase ->
                when (code) {
                    GoogleBillingImpl.OK -> consumePurchaseList(payType, listPurchase, CONSUME_FROM_PAY)
                    GoogleBillingImpl.USER_CANCELED -> {
                        ToastUtils.showShort(R.string.hc_word_cancel)
                        trackAndLogPay(step = "支付回调：取消支付", error_code = code.toString(), error_msg = msg, isShowToast = false)
                    }

                    else -> trackAndLogPay(step = "支付回调：支付失败", error_code = code.toString(), error_msg = msg)
                }
            }
        }
        Log.i(TAG, "初始化连接google服务")
        fragmentActivity = mainF.activity
        connectGP()
    }

    /**
     * 退出支付页面
     */
    fun onDestroy() {
        Log.w(TAG, "断开google服务")
        GoogleBillingImpl.endConnection()
        weakHandler.removeCallbacksAndMessages(null)
        fragmentActivity = null
    }

    /**
     * 1.0查询给用户是否有缓存未处理订单
     */
    fun checkOrderCache() {
        Log.i(TAG, "开始查询补单  uid=${getMyUid()} isConnected=${GoogleBillingImpl.isReady()}")
        if (getMyUid() > 0L) {
            if (GoogleBillingImpl.isReady()) {
                queryPurchases()
                queryNotConfirm()
            } else {
                reConnectCount = 5
                retryConnect(isImmediately = true, from = "checkOrderCache")
            }
        } else {
            Log.e(TAG, "checkOrderCache 未登录")
        }
    }

    /**
     * 1.1查询未确认完毕的订单
     */
    private fun queryNotConfirm() {
        val noConfirmOrderMap = getOrderMap()
        Log.i(TAG, "本地已支付未确认订单数量：${noConfirmOrderMap.size}")
        if (noConfirmOrderMap.isEmpty()) return
        noConfirmOrderMap.forEach {
            reqConfirmPayOrder(it.value, CONSUME_FROM_CHECK)
        }
    }

    /**
     * 1.2查询历史订单是否有未被消耗的
     */
    private fun queryPurchases() {
        Log.i(TAG, "开始查询交易缓存")
        GoogleBillingImpl.queryPurchasesAsync(
            callback = { code, msg, listPurchase -> checkConsume(GoogleBillingImpl.IN_APP, code, msg, listPurchase) },
            subCallback = { code, msg, listPurchase -> checkConsume(GoogleBillingImpl.SUBS, code, msg, listPurchase) }
        )
    }

    /**
     * 1.3检查并消费
     */
    private fun checkConsume(payType: String, code: Int, msg: String?, purchases: List<CopyPurchase>?) {
        Log.i(TAG, "查询到的交易缓存数量 type=$payType code=$code msg=$msg purchases=$purchases")
        if (code == GoogleBillingImpl.OK && !purchases.isNullOrEmpty()) {
            consumePurchaseList(payType, purchases, CONSUME_FROM_CHECK)
        } else {
            //查询交易缓存失败 或无缓存
            if (!purchases.isNullOrEmpty()) {
                trackAndLogPay(step = "queryPurchases_1.1", error_code = code.toString(), error_msg = msg, isShowToast = false)
            }
        }
    }

    /**
     * 2.连接google服务
     */
    private fun connectGP() {
        if (!GooglePayAuthImpl.isGpAvailable()) {
            trackAndLogPay(
                step = "连接Google服务1.0",
                error_code = GooglePayAuthImpl.googleServiceAvailableCode(Utils.getApp()).toString(),
                error_msg = GooglePayAuthImpl.googleServiceAvailableMsg(),
                isShowToast = false
            )
            return
        }

        if (GoogleBillingImpl.isReady() || isConnecting) {
            Log.e(TAG, "google服务已连接初始化过了或正在链接中")
            return
        }

        isConnecting = true
        GoogleBillingImpl.startConnection { code, msg ->
            isConnecting = false
            when (code) {
                GoogleBillingImpl.OK -> {
                    Log.i(TAG, "Google服务连接回调  code=$code,msg=$msg")
                    reConnectCount = 5
                    //查询支付未完成缓存，补单
                    checkOrderCache()
                }

                else -> retryConnect(from = "connectGP", msg = msg)
            }
        }
    }

    /**
     * 2.1尝试重连
     */
    private fun retryConnect(isImmediately: Boolean = false, from: String, msg: String? = "Google Service Not Ready", isShowToast: Boolean = false) {
        if (reConnectCount > 0 || isImmediately) {
            weakHandler.removeCallbacksAndMessages(null)
            weakHandler.postDelayed({
                reConnectCount--
                Log.e(TAG, "google服务重连")
                trackAndLogPay(step = "google服务重连", error_code = from, error_msg = "$msg 重连第 $reConnectCount 次", isShowToast = isShowToast)
                connectGP()
            }, if (isImmediately) 10 else 5000L)
        }
    }

    /**
     *  3.根据商品id查询支付档位信息
     */
    private fun queryProductDetail(productId: String?, orderId: String?) {
        Log.i(TAG, "开始查询支付档位信息 productId=$productId")
        GoogleBillingImpl.queryProductDetailsAsync("$productId", payType) { code, msg, productSize ->
            if (code == GoogleBillingImpl.OK && productSize > 0) {
                Log.i(TAG, "支付档位查询结果 size=$productSize")
                launchPay(productId, orderId)
            } else {
                trackAndLogPay(step = "支付档位查询失败1.1", error_code = code.toString(), error_msg = msg)
            }
        }
    }

    /**
     * 4.调起google支付界面
     */
    private fun launchPay(productId: String?, orderId: String?) {
        fragmentActivity?.let {
            GoogleBillingImpl.launchPurchase(it, getMyUid().toString(), "$orderId", "$productId") { code, msg ->
                when (code) {
                    GoogleBillingImpl.OK -> Log.w(TAG, "拉起支付弹窗成功")
                    else -> trackAndLogPay(step = "launchPay_1.1", error_code = code.toString(), error_msg = msg)
                }
            }
        }
    }

    /**
     *  5.筛选能处理的订单
     */
    private fun consumePurchaseList(payType: String, purchases: List<CopyPurchase>?, from: String) {
        Log.i(TAG, "筛选能消费的订单 type=$payType purchase=$purchases")
        purchases.takeIf { !it.isNullOrEmpty() }?.forEach { purchase ->
            when (payType) {
                //一次性消费
                GoogleBillingImpl.IN_APP -> consumePurchase(purchase, from)
                //订阅消费
                GoogleBillingImpl.SUBS -> consumePurchaseSub(purchase, from)
                else -> Log.e(TAG, "非法消费类型")
            }
        } ?: run { Log.e(TAG, "无可消费订单") }
    }

    /**
     * 6.订阅商品确认购买交易
     */
    private fun consumePurchaseSub(purchase: CopyPurchase, from: String) {
        Log.i(TAG, "consumePurchaseSub 开始消耗订阅项目")
        purchase.payType = GoogleBillingImpl.SUBS
        GoogleBillingImpl.consumePurchaseSub(purchase) { code, msg, token ->
            when (code) {
                GoogleBillingImpl.OK -> reqConfirmPayOrder(purchase, from)
                else -> trackAndLogPay(step = "consumePurchaseSub $from", error_code = code.toString(), error_msg = "$msg $token", isShowToast = false)
            }
        }
    }

    /**
     * 6.1消耗已购买的项目
     */
    private fun consumePurchase(purchase: CopyPurchase, from: String) {
        Log.i(TAG, "开始消耗商品")
        purchase.payType = GoogleBillingImpl.IN_APP
        GoogleBillingImpl.consumePurchase(purchase) { code, msg, token ->
            when (code) {
                GoogleBillingImpl.OK -> reqConfirmPayOrder(purchase, from)
                else -> trackAndLogPay(step = "consumePurchase $from", error_code = code.toString(), error_msg = "$msg $token", isShowToast = false)
            }
        }
    }

    /**
     * 支付错误或中断埋点上报
     * @param step          支付步骤节点
     * @param result        支付结果
     * @param error_code    错误码
     * @param error_msg     错误信息
     */
    private fun trackAndLogPay(step: String, result: Int = 0, error_code: String, error_msg: String?, isShowToast: Boolean = true) {
        //通知支付结果
        //LiveEventManager.post(EventPayResult(EventPayResult.PAY_FAIL))

        if (result == 0) {
            Log.e(TAG, "$step error_code=$error_code error_msg=$error_msg")
            if (isShowToast) ToastUtils.showLong(R.string.hc_toast_try_again, error_code)
        }
        //TraceManager.traceGooglePay(chargeSource, step, chargeId, result, if (isFirstCharge) 1 else 0, financeOrderId, error_code, "error_msg=${error_msg}  purchase=$tracePurchase")
    }

    /**
     * 1.1创建订单支付
     */
    fun createOrder(data: GoodsEntity) {
        payType = if (data.pay_type == GoodsEntity.PAY_TYPE_SUBS) GoogleBillingImpl.SUBS else GoogleBillingImpl.IN_APP

        if (!GoogleBillingImpl.isReady()) {
            retryConnect(isImmediately = true, from = "createOrder", msg = "GoogleService Unavailable , Please check Google Play!", isShowToast = reConnectCount <= 3)
            return
        }

        Log.i(TAG, "开始创建订单 档位信息=$data")

        val params = mutableMapOf<String, Any?>()
        params["platform"] = "google"
        params["product_id"] = data.product_id

        fragmentActivity?.let { activity ->
            HttpManager.get(activity, URLApi.URL_CREATE_PAY_ORDER, params, object : RequestCallBack<OrderPayBean?> {
                override fun onSuccess(data: OrderPayBean?) {
                    if (data == null) {
                        trackAndLogPay(step = "订单创建失败_1", error_code = PAY_ERROR_CREATE_ORDER_FAIL, error_msg = "创建订单无数据返回")
                        return
                    }
                    Log.i(TAG, "订单创建成功 $data")
                    queryProductDetail(data.product_id, data.order_id)
                }

                override fun onFail(code: Int, msg: String?) {
                    trackAndLogPay(step = "订单创建失败_2", error_code = code.toString(), error_msg = msg)
                }
            })
        }
    }

    /**
     * 确认订单
     */
    private fun reqConfirmPayOrder(purchase: CopyPurchase, from: String) {
        saveConfirmOrder(purchase)

        val params = mutableMapOf<String, Any?>()
        params["pay_items_order_id"] = purchase.financeOrderId
        params["purchase_data"] = purchase.originalJson
        params["data_signature"] = purchase.signature

        fragmentActivity?.let { activity ->
            HttpManager.postBody(activity, URLApi.URL_CONFIRM_SUB_ORDER, params, object : RequestCallBack<PaymentBean?> {
                override fun onSuccess(data: PaymentBean?) {
                    Log.w(TAG, "订阅请求确认完毕，交易结束 bean=$data")
                    trackAndLogPay(step = "订阅请求确认完毕，交易结束", result = 1, error_code = "0", error_msg = "交易结束 bean=$data", isShowToast = false)
                    if (from == CONSUME_FROM_PAY) ToastUtils.showShort(StringUtils.getString(R.string.hc_word_confirm))

                    //删除缓存
                    removeConfirmOrder(purchase)

                    //通知支付结果
                    LiveEventManager.post(EventPayResult(EventPayResult.PAY_SUCCESS))
                }

                override fun onFail(code: Int, msg: String?) {
                    trackAndLogPay(step = "订阅请求确认订单失败", error_code = code.toString(), error_msg = msg, isShowToast = from == CONSUME_FROM_PAY)
                }
            })
        }
    }

    /**
     * 获取未确认的订单
     */
    private fun getOrderMap(): MutableMap<String, CopyPurchase> {
        val mapStr = MMKVManager.getString(MMKVKey.NO_CONFIRM_ORDER)
        return if (mapStr.isEmpty()) mutableMapOf() else {
            fromJson<MutableMap<String, CopyPurchase>>(mapStr, genericType<MutableMap<String, CopyPurchase>>()) ?: mutableMapOf()
        }
    }

    /**
     * 保存未确认的订单
     */
    private fun saveConfirmOrder(purchase: CopyPurchase) {
        Log.i(TAG, "确认订单前保存 ${purchase.gpOrderId}")
        val orderMap = getOrderMap().apply { put(purchase.gpOrderId, purchase) }
        MMKVManager.put(MMKVKey.NO_CONFIRM_ORDER, orderMap.toJson())
    }

    /**
     * 删除已确认的订单
     */
    private fun removeConfirmOrder(purchase: CopyPurchase) {
        val old = getOrderMap()
        if (old.isNotEmpty()) {
            old.remove(purchase.gpOrderId)
            Log.i(TAG, "确认订单后删除 ${purchase.gpOrderId} size=${old.size}")
            //更新删除之后的
            MMKVManager.put(MMKVKey.NO_CONFIRM_ORDER, old.toJson())
        }
    }

    private fun getMyUid() = GlobalUserManager.getUid()
}