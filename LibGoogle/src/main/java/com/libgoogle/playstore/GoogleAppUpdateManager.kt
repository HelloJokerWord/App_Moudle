package com.libgoogle.playstore

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.updatePriority
import com.third.libcommon.constant.GlobalConstant


/**
 * Created on 2022/12/27.
 * @author Joker
 * Des: google应用内升级
 */

object GoogleAppUpdateManager {

    private const val TAG = "GooglePlayManager"
    private var appUpdateManager: AppUpdateManager? = null
    private val listener = InstallStateUpdatedListener {
        Log.i(TAG, "state=$it")
        when (it.installStatus) {
            InstallStatus.DOWNLOADING -> {
                val bytesDownloaded = it.bytesDownloaded()
                val totalBytesToDownload = it.totalBytesToDownload()
                Log.i(TAG, "bytesDownloaded=$bytesDownloaded , totalBytesToDownload=$totalBytesToDownload")

            }

            InstallStatus.DOWNLOADED -> {
                Log.i(TAG, "下载完成")
                appUpdateManager?.completeUpdate()
            }

            InstallStatus.CANCELED -> {
            }

            InstallStatus.FAILED -> {
            }

            InstallStatus.INSTALLED -> {
            }

            InstallStatus.INSTALLING -> {
            }

            InstallStatus.PENDING -> {
            }

            InstallStatus.UNKNOWN -> {
            }

            else -> {

            }
        }
    }

    /**
     * 跳转到google应用市场app详情页
     */
    fun jumpGooglePlayStore(context: Context?, packageName: String?) {
        if (context == null || packageName.isNullOrEmpty()) return
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            setPackage("com.android.vending")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * 初始化app更新管理器
     */
    fun initAppUpdateManager(activity: Activity?) {
        if (activity == null) return
        //创建管理器实例
        appUpdateManager = AppUpdateManagerFactory.create(activity)
    }

    /**
     * 检查并更新
     */
    fun checkAppUpdate(activity: Activity?) {
        if (activity == null) return

        //返回用于检查更新的意图对象,检查平台是否允许指定类型的更新
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener {
            Log.i(
                TAG, "enable=${it.updateAvailability()}," +
                        "isImmediate=${it.isImmediateUpdateAllowed}," +
                        "code=${it.availableVersionCode()}," +
                        "installStatus=${it.installStatus()}," +
                        "day=${it.clientVersionStalenessDays}," +
                        "priority=${it.updatePriority}"
            )

            //是否允许更新
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                //google play商店通知用户更新以来已过去多少时间
                && (it.clientVersionStalenessDays ?: -1) >= 7
                //检查优先级
                && it.updatePriority >= 5
                //检查更新模式
                && it.isImmediateUpdateAllowed
            ) {
                //符合条件开始更新
                try {
                    appUpdateManager?.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, activity, GlobalConstant.REQ_CODE_GOOGLE_UPDATE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 注册更新下载过程
     */
    fun registerUpdateListener() {
        unRegisterUpdateListener()
        appUpdateManager?.registerListener(listener)
    }

    /**
     * 关闭监听
     */
    fun unRegisterUpdateListener() {
        appUpdateManager?.unregisterListener(listener)
    }

    /**
     * 回到主界面检查
     */
    fun onResume(activity: Activity?) {
        if (activity == null) return
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager?.completeUpdate()
            } else if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager?.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, activity, GlobalConstant.REQ_CODE_GOOGLE_UPDATE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "requestCode=$requestCode resultCode=$resultCode data=$data")
        when (requestCode) {
            //app更新
            GlobalConstant.REQ_CODE_GOOGLE_UPDATE -> {
                when (resultCode) {
                    //用户已接受更新。对于立即更新，您可能不会收到此回调，因为在将控制权交还给您的应用之前，更新应该已经完成了。
                    Activity.RESULT_OK -> {

                    }
                    //用户已拒绝或取消更新。
                    Activity.RESULT_CANCELED -> {

                    }
                    //更新错误
                    else -> {
                        ToastUtils.showShort("应用更新错误")
                    }
                }
            }
        }
    }
}