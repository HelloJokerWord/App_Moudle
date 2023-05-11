package com.third.libcommon.websocket

import com.third.libcommon.http.HttpManager
import com.third.libcommon.websocket.socket.BusinessSocketClient
import com.third.libcommon.websocket.socket.SocketURL

/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:接收数据 LiveEventManager.observe(owner, EventBusinessSocket::class.java) {}
 */

class BusinessManager {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { BusinessManager() }
    }

    private var businessSocketClient: BusinessSocketClient? = null
    private var lastConnectTime = 0L

    /**
     * 开始链接
     */
    fun startConnect() {
        //1、检查是否已连接 / 是否未登录 / 防抖动多次启动
        val curTime = System.currentTimeMillis()
        //if (isSocketConnected() || HCUserManager.isLogin() ||(curTime - lastConnectTime < 1500L)) return
        if (isSocketConnected() || (curTime - lastConnectTime < 1500L)) return
        lastConnectTime = curTime

        //2、创建连接
        closeSocket()
        businessSocketClient = BusinessSocketClient(SocketURL.URL_BUSINESS + HttpManager.getCommonParamStr())
        businessSocketClient?.connectionLostTimeout = 20
        businessSocketClient?.connect()
    }

    fun checkAndReconnectSocket() {
        //if (HCUserManager.isLogin() && !isSocketConnected()) {
        if (!isSocketConnected()) {
            startConnect()
        }
    }

    /**
     * 关闭连接
     */
    fun closeSocket() {
        businessSocketClient?.close()
        businessSocketClient = null
    }

    /**
     * 发送消息
     */
    fun doSendMsg(data: String?) {
        businessSocketClient?.doSendMsg(data)
    }

    /**
     * 子线程中发送
     */
    fun doSendHeartBeat() {

    }

    /**
     * 长链是否连着
     */
    fun isSocketConnected() = businessSocketClient?.isOpen == true


}