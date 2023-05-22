package com.example.appmoudle.manager.resdownload

import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileUtils
import com.third.libcommon.constant.GlobalPath
import java.io.File

/**
 * Created on 2023/2/17.
 * @author Joker
 * Des:
 */

class WearAvatarResManager : BaseResManager() {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { WearAvatarResManager() }
    }

    override fun getResType(): String = "WearAvatarResManager"

    override fun resScenes(): Int = RES_TYPE_AVATAR_FRAME

    override fun getFileName(resUrl: String?): String = "wear_avatar_${EncryptUtils.encryptMD5ToString(resUrl)}"

    override fun getRootPath(): String = GlobalPath.WEAR_AVATAR_SVGA

    override fun getResPath(resUrl: String?): String = "${getRootPath()}${getFileName(resUrl)}"

    override fun getResFile(resUrl: String?): File = FileUtils.getFileByPath(getResPath(resUrl))

    fun init(owner: LifecycleOwner) {
        reqFileRes(owner)
    }
}