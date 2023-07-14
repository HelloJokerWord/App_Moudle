package com.libgoogle.billing

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object GooglePayAuthImpl {

    /**
     *  https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#isGooglePlayServicesAvailable(android.content.Context)
     */
    fun googleServiceAvailableCode(context: Context): Int {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
    }

    fun googleServiceAvailableMsg(): String {
        return GoogleApiAvailability.getInstance().getErrorString(googleServiceAvailableCode(Utils.getApp()))
    }

    /**
     * google应用市场是否可用
     */
    fun isGpAvailable() = googleServiceAvailableCode(Utils.getApp()) == ConnectionResult.SUCCESS
}