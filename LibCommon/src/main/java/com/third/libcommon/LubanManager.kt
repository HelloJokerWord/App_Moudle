package com.third.libcommon

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.third.libcommon.constant.GlobalPath
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.Locale


/**
 * Created on 2022/9/13.
 * @author Joker
 * Des: luban图片压缩工具  https://github.com/Curzibn/Luban/tree/turbo
 */

object LubanManager {
    private const val TAG = "LubanManager"

    /**
     * 压缩图片文件
     * @param oriFile       原图文件
     *
     */
    fun compress(context: Context?, oriFile: File?, result: (file: File?) -> Unit) {
        if (context == null || !FileUtils.isFileExists(oriFile)) {
            result.invoke(null)
            return
        }

        Log.i(TAG, "压缩前 onStart oriFileSize=${FileUtils.getSize(oriFile)} threat=${Thread.currentThread().name}")
        launchCompress(Luban.with(context).load(oriFile), result)
    }

    fun compress(context: Context?, oriFilePath: String?, result: (file: File?) -> Unit) {
        if (context == null || !FileUtils.isFileExists(oriFilePath)) {
            result.invoke(null)
            return
        }

        Log.i(TAG, "压缩前 onStart oriFilePath=${oriFilePath} oriFileSize=${FileUtils.getSize(oriFilePath)} threat=${Thread.currentThread().name}")
        launchCompress(Luban.with(context).load(oriFilePath), result)
    }

    fun compress(context: Context?, oriFileUri: Uri?, result: (file: File?) -> Unit) {
        if (context == null || oriFileUri == null) {
            result.invoke(null)
            return
        }

        Log.i(TAG, "压缩前 onStart oriFileSize=${FileUtils.getSize(oriFileUri.toString())} threat=${Thread.currentThread().name}")
        launchCompress(Luban.with(context).load(oriFileUri), result)
    }

    private fun launchCompress(builder: Luban.Builder, result: (file: File?) -> Unit) {
        builder.ignoreBy(2000)
            .setTargetDir(GlobalPath.getLubanTargetPath())
//            .setFocusAlpha(false)
            .filter { path ->
                !(TextUtils.isEmpty(path)
                        || path.lowercase(Locale.getDefault()).endsWith(".gif")
                        || path.lowercase(Locale.getDefault()).endsWith(".tiff"))
            }
//            .setRenameListener { filePath ->
//                try {
//                    val md = MessageDigest.getInstance("MD5")
//                    md.update(filePath.toByteArray())
//                    return@setRenameListener BigInteger(1, md.digest()).toString(32)
//                } catch (e: NoSuchAlgorithmException) {
//                    e.printStackTrace()
//                }
//                return@setRenameListener ""
//            }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {}

                override fun onSuccess(file: File?) {
                    Log.i(TAG, "压缩后 path=${file?.absolutePath}  targetSize=${FileUtils.getSize(file)}  threat=${Thread.currentThread().name}")
                    result.invoke(file)
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    result.invoke(null)
                }
            })
            .launch()
    }


}