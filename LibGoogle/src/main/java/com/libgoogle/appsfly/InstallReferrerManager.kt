package com.libgoogle.appsfly

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener

/**
 * Created on 2022/8/15.
 * @author Joker
 * Des: 注意：安装引荐来源信息的有效期为 90 天，并且在用户重新安装应用之前不会发生变化。为避免应用中进行不必要的 API 调用，您应该在安装完成后首次执行期间仅调用 API 一次。
 * @link https://developer.android.com/google/play/installreferrer/library#kotlin
 */

object InstallReferrerManager {

    private const val TAG = "InstallReferrerManager"
    private var referrerClient: InstallReferrerClient? = null

    fun startConnection(context: Context, installReferrerListener: ((result: InstallReferrerResult) -> Unit)? = null) {
        referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient?.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                try {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            // Connection established.
                            val referrerDetails = referrerClient?.installReferrer
                            val resultInstall = InstallReferrerResult(
                                code = InstallReferrerClient.InstallReferrerResponse.OK,
                                msg = "SUCCESS",
                                referrerUrl = referrerDetails?.installReferrer,
                                referrerClickTime = referrerDetails?.referrerClickTimestampSeconds ?: 0L,
                                appInstallTime = referrerDetails?.installBeginTimestampSeconds ?: 0L,
                                instantExperienceLaunched = referrerDetails?.googlePlayInstantParam ?: false,
                            )
                            Log.i(TAG, "resultInstall=$resultInstall")
                            installReferrerListener?.invoke(resultInstall)
                        }
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            Log.e(TAG, "FEATURE_NOT_SUPPORTED")
                            // API not available on the current Play Store app.
                            installReferrerListener?.invoke(InstallReferrerResult(code = InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED, msg = "FEATURE_NOT_SUPPORTED"))
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            Log.e(TAG, "SERVICE_UNAVAILABLE")
                            // Connection couldn't be established.
                            installReferrerListener?.invoke(InstallReferrerResult(code = InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE, msg = "SERVICE_UNAVAILABLE"))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.e(TAG, "onInstallReferrerServiceDisconnected")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                installReferrerListener?.invoke(InstallReferrerResult(code = InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED, msg = "SERVICE_DISCONNECTED"))
            }
        })
    }
}
