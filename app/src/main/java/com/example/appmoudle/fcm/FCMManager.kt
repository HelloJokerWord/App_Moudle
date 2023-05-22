package com.example.appmoudle.fcm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.NotificationUtils
import com.blankj.utilcode.util.TimeUtils
import com.example.appmoudle.R
import com.example.appmoudle.config.GlobalUserManager
import com.example.appmoudle.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.third.libcommon.extension.fromJson
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack
import com.third.libcommon.http.URLApi


/**
 * Created on 2022/10/27.
 * @author Joker
 * Des:
 */

object FCMManager {

    const val TOKEN_KEY = "FCM_TOKEN"

    const val FCM_JUMP_URL = "FCM_JUMP_URL"
    const val FCM_TASK_ID = "FCM_TASK_ID"
    const val FCM_ARRIVE_TIME = "FCM_ARRIVE_TIME"

    private var notificationId = 0

    /**
     * 有更新也上传一次
     */
    fun getAndUploadToken(activity: FragmentActivity) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(MyFirebaseMessagingService.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            uploadFCMToken(activity, token)
        })
    }

    /**
     * 登陆成功，上报token
     */
    fun uploadFCMToken(activity: FragmentActivity, token: String) {
        Log.i(MyFirebaseMessagingService.TAG, "uploadFCMToken=$token")
        if (token.isEmpty()) return
        HttpManager.postBody(activity, URLApi.URL_POST_FCM_TOKEN, mutableMapOf("client" to 1, "token" to token), object : RequestCallBack<String> {
            override fun onSuccess(data: String) {

            }

            override fun onFail(code: Int, msg: String?) {
            }
        })
    }

    fun autoInitEnabled(enable: Boolean) {
        Firebase.messaging.isAutoInitEnabled = enable
    }

    fun setNotification(context: Context, data: String?, from: String) {
        try {
            Log.i(MyFirebaseMessagingService.TAG, "from=$from data=$data")

            if (data.isNullOrEmpty()) return

            val isForeground = AppUtils.isAppForeground()
            val isMainActivityAlive = MainActivity.isMainActivityAlive
            val notificationEnable = NotificationUtils.areNotificationsEnabled()
            val msgEntity = fromJson(data, FCMMessageEntity::class.java)

            Log.i(
                MyFirebaseMessagingService.TAG,
                "uid=${msgEntity?.notification_receive_uid} " +
                        "notificationEnable=$notificationEnable " +
                        "isForeground=$isForeground " +
                        "isMainActivityAlive=$isMainActivityAlive"
            )

            //不是自己的，都过滤掉
            if (msgEntity?.notification_receive_uid != GlobalUserManager.getUid()
                || msgEntity.notification_receive_uid == 0L
                || !notificationEnable
                || (isForeground && isMainActivityAlive)
            ) return

            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(FCM_JUMP_URL, msgEntity.notification_jump_url)
            intent.putExtra(FCM_TASK_ID, msgEntity.notification_task_id)
            intent.putExtra(FCM_ARRIVE_TIME, TimeUtils.getNowMills())
            val pendingIntent = PendingIntent.getActivity(
                context,
                TimeUtils.getNowMills().toInt(),
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            )

            val vibrates = longArrayOf(0, 1000, 1000, 1000)
            NotificationUtils.notify(notificationId++) {
                it.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(TimeUtils.getNowMills())
                    .setContentTitle(msgEntity.notification_title)
                    .setContentText(msgEntity.notification_content)
                    .setAutoCancel(true)
                    .setVibrate(vibrates)
                    .setContentIntent(pendingIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}