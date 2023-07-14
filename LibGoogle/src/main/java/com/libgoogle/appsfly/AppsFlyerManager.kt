package com.libgoogle.appsfly

import android.app.Application
import android.content.Context
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.MetaDataUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.Utils
import com.libgoogle.BuildConfig
import com.third.libcommon.constant.GlobalConstant


/**
 * Created on 2022/7/27.
 * @author Joker
 * Des:
 */

object AppsFlyerManager {

    private const val TAG = "AppsFlyerManager"
    private val appsFlyerId by lazy { getAppFlyerId(Utils.getApp()) }

    /**
     * 初始化 appsflyer
     */
    fun initAppsFlyer(application: Application, onSuccess: ((data: MutableMap<String, Any?>?) -> Unit)? = null) {
        val appsflyerKey = MetaDataUtils.getMetaDataInApp("appsflyer_key")
        Log.i(TAG, "initAppsFlyer appsflyerKey=$appsflyerKey")
        val appsflyer = AppsFlyerLib.getInstance()
        // For debug - remove in production
        appsflyer.setDebugLog(BuildConfig.DEBUG)
        //optional
        appsflyer.setMinTimeBetweenSessions(0)
        appsflyer.setCustomerUserId(GlobalConstant.deviceTag)
        appsflyer.init(appsflyerKey, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(p0: MutableMap<String, Any?>?) {
                Log.i(TAG, "onConversionDataSuccess p0=$p0")
                onSuccess?.invoke(p0)
            }

            override fun onConversionDataFail(p0: String?) {
                Log.i(TAG, "onConversionDataFail p0=$p0")
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                Log.i(TAG, "onAppOpenAttribution p0=$p0")
            }

            override fun onAttributionFailure(p0: String?) {
                Log.i(TAG, "onAttributionFailure p0=$p0")
            }
        }, application)
        appsflyer.start(application)
    }

    /**
     * 获取appsFlyerId
     */
    fun getAppFlyerId(context: Context): String? {
        Log.i(TAG, "getAppFlyerId")
        return AppsFlyerLib.getInstance().getAppsFlyerUID(context)
    }

    /**
     * 登陆
     */
    fun onLoginSuccess(context: Context, uid: String, loginType: String) {
        Log.i(TAG, "onLogin $uid  appsFlyerId=$appsFlyerId")
        AppsFlyerLib.getInstance().setCustomerUserId(uid)
        val hashMap = mutableMapOf<String, Any>()
        hashMap["af_registration_method"] = loginType
        AppsFlyerLib.getInstance().logEvent(context, "af_complete_registration", hashMap)
    }

    /**
     * 支付结果
     */
    fun onPaySuccess(context: Context, uid: String, af_revenue: String, af_currency: String, gpId: String, orderId: String) {
        Log.i(TAG, "onPayResult appsFlyerId=$appsFlyerId")
        AppsFlyerLib.getInstance().setCustomerUserId(uid)
        val hashMap = mutableMapOf<String, Any>()
        hashMap["af_revenue"] = af_revenue
        hashMap["af_currency"] = af_currency
        hashMap["app_version"] = AppUtils.getAppVersionName()
        hashMap["time"] = TimeUtils.getNowString()
        hashMap["app_name"] = AppUtils.getAppPackageName()
        hashMap["gpId"] = gpId
        hashMap["orderId"] = orderId
        AppsFlyerLib.getInstance().logEvent(context, "af_add_payment_info", hashMap)
    }
}