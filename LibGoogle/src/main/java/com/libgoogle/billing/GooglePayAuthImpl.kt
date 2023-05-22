package com.libgoogle.billing

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object GooglePayAuthImpl {

    const val AVAILABLE_CODE_SUCCESS = ConnectionResult.SUCCESS

    fun googleServiceAvailableCode(context: Context): Int {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
    }

    fun googleServiceAvailableMsg(code: Int): String {
        return GoogleApiAvailability.getInstance().getErrorString(code)
    }
}