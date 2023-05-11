package com.third.libcommon.websocket

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLParameters

/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:
 */

abstract class BaseSocketClient : WebSocketClient {

    constructor(serverUri: URI?, draft: Draft?) : super(serverUri, draft) {
        Log.i(TAG, "serverURI=$serverUri")
    }

    constructor(serverURI: URI?) : super(serverURI) {
        Log.i(TAG, "serverURI=$serverURI")
    }

    constructor(serverUri: URI?, httpHeaders: Map<String?, String?>?) : super(serverUri, httpHeaders) {
        Log.i(TAG, "serverURI=$serverUri httpHeaders=$httpHeaders")
    }

    companion object {
        const val TAG = "BaseSocketClient"
    }

    /**
     * 接收数据
     * @param messageType   广播业务类型
     * @param payload       业务json数据，根据message_type去解析
     * @param decryptedMsg  解密后原始json数据
     */
    abstract fun receiveMsg(messageType: Int, payload: String?, decryptedMsg: String?)

    /**
     * 建立长连接成功
     */
    override fun onOpen(handshake: ServerHandshake?) {
        Log.i(TAG, "open status=${handshake?.httpStatus} msg=${handshake?.httpStatusMessage}")
    }

    /**
     * 收到推送消息 一般是jsonString
     */
    override fun onMessage(message: String?) {
        try {
            if (message.isNullOrEmpty()) {
                Log.e(TAG, "接收消息为空")
                return
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 长链接关闭
     */
    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.e(TAG, "onClose code=$code reason=$reason remote=$remote")
    }

    /**
     * 长链接错误
     */
    override fun onError(e: Exception) {
        Log.e(TAG, "onError ${e.message}")
        e.printStackTrace()
    }

    override fun onSetSSLParameters(sslParameters: SSLParameters?) {
        //处理以下问题
        //https://github.com/TooTallNate/Java-WebSocket/wiki/No-such-method-error-setEndpointIdentificationAlgorithm
    }

    /**
     * 发送长链接数据
     */
    fun doSendMsg(data: String?) {
        try {
            Log.d(TAG, "发送=$data")
            if (data.isNullOrEmpty()) {
                Log.e(TAG, "发送消息为空")
                return
            }

            if (isOpen) send(data)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}