package com.example.appmoudle.fcm

import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.SPStaticUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Created on 2022/10/24.
 * @author Joker
 * Des:
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "HCFirebaseMessagingService"
    }

    override fun onCreate() {
        super.onCreate()
        //Log.i(TAG, "onCreate")
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        //Log.i(TAG, "intent=${intent?.extras}")
        val data = intent?.getStringExtra("msg_data")
        FCMManager.setNotification(this, data, "FCM")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //Log.i(TAG, "FCM receive id=${message.messageId} message=${message.data}")
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.w(TAG, "onDeletedMessages")
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
        Log.i(TAG, "send msgId=$msgId")
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.e(TAG, "error msgId=$msgId  exception=${exception.message}")
    }

    /**
     * 监控令牌的生成
     * 每当生成新令牌时，都会触发 onNewToken 回调函数。
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "onNewToken=$token")
        SPStaticUtils.put(FCMManager.TOKEN_KEY, token) //保存，登陆成功后上报
    }
}