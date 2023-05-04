package com.third.libcommon

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.FileUtils
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
     * @param targetPath    压缩后图片路径 一般用默认路径
     *
     */
    fun compress(context: Context?, oriFile: File?, targetPath: String?, result: (file: File?) -> Unit) {
        if (context == null || oriFile == null) {
            result.invoke(null)
            return
        }

        Log.i(TAG, "压缩前 onStart oriFileSize=${FileUtils.getSize(oriFile)} threat=${Thread.currentThread().name}")
        launchCompress(Luban.with(context).load(oriFile), targetPath, result)
    }

    fun compress(context: Context?, oriFilePath: String?, targetPath: String?, result: (file: File?) -> Unit) {
        if (context == null || oriFilePath.isNullOrEmpty()) {
            result.invoke(null)
            return
        }

        Log.i(TAG, "压缩前 onStart oriFileSize=${FileUtils.getSize(oriFilePath)} threat=${Thread.currentThread().name}")
        launchCompress(Luban.with(context).load(oriFilePath), targetPath, result)
    }

    fun compress(context: Context?, oriFileUri: Uri?, targetPath: String?, result: (file: File?) -> Unit) {
        if (context == null || oriFileUri == null) {
            result.invoke(null)
            return
        }

        Log.i(TAG, "压缩前 onStart oriFileSize=${FileUtils.getSize(oriFileUri.path)} threat=${Thread.currentThread().name}")
        launchCompress(Luban.with(context).load(oriFileUri), targetPath, result)
    }

    private fun launchCompress(builder: Luban.Builder, targetPath: String?, result: (file: File?) -> Unit) {
        builder.ignoreBy(50) //单位k
            .setTargetDir(targetPath)
            .filter { path -> !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault()).endsWith(".gif")) }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {}

                override fun onSuccess(file: File?) {
                    Log.i(TAG, "压缩后 path=${file?.path}  targetSize=${FileUtils.getSize(file)}  threat=${Thread.currentThread().name}")
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