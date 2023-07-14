package com.third.libcommon

import android.content.Context
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import linc.com.heifconverter.HeifConverter
import java.io.File

/**
 * CreateBy:Joker
 * CreateTime:2023/7/4 11:56
 * description：
 */
object HeicConvertManager {

    private const val TAG = "HeicConvertManager"

    fun heic2png(context: Context?, file: File, coverImg: (file: File?) -> Unit) {
        if (context == null) return
        HeifConverter.useContext(context)
            .fromFile(file.path)
            .withOutputFormat(HeifConverter.Format.PNG)
            .withOutputQuality(100) // optional - default value = 100. Available range (0 .. 100)
            .saveFileWithName("IMG_${EncryptUtils.encryptMD5ToString(file.path)}")
            .saveResultImage(true) // optional - default value = true
            .convert {
                LogUtils.i("$TAG heicCover = $it")
                coverImg.invoke(FileUtils.getFileByPath(it[HeifConverter.Key.IMAGE_PATH] as String?))
            }
    }
}