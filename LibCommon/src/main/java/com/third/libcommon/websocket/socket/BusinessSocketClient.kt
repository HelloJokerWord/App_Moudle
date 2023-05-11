package com.third.libcommon.websocket.socket

import com.third.libcommon.websocket.BaseSocketClient
import java.net.URI

/**
 * Created on 2022/9/8.
 * @author Joker
 * Des: 业务socket
 */
class BusinessSocketClient(url: String) : BaseSocketClient(URI(url)) {

    /**
     * 接收数据
     * @param messageType   广播业务类型
     * @param payload       业务json数据，根据message_type去解析
     * @param decryptedMsg  解密后原始json数据
     */
    override fun receiveMsg(messageType: Int, payload: String?, decryptedMsg: String?) {

    }
}