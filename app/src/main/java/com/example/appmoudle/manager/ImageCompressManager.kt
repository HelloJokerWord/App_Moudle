package com.example.appmoudle.manager

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ThreadUtils
import com.example.appmoudle.config.HCPath
import top.zibin.luban.Luban
import java.io.File
import java.util.*


/**
 * Created on 2022/9/13.
 * @author Joker
 * Des: luban图片压缩工具  https://github.com/Curzibn/Luban/tree/turbo
 */

object ImageCompressManager {
    private const val TAG = "ImageCompressManager"

    /**
     * 压缩一张图
     *
     * @param uri 原图片的地址uri
     * @param targetPath 生成图片的地址
     * @param custom 自定义压缩的回调
     */
    fun compress(context: Context?, uri: Uri, targetPath: String, custom: ((builder: Luban.Builder) -> Unit)? = null): MutableLiveData<File?> {
        val result = MutableLiveData<File?>()
        try {
            ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<File?>() {
                override fun doInBackground(): File? {
                    Log.i(TAG, "原来大小 = ${FileUtils.getSize(uri.path)}")
                    //Uri转Files （可能存在失败）
                    val targetEmptyFiles = transformUri2File(context, arrayListOf(uri))
                    //遍历判断是否有转换失败的情况
                    val targetFiles = requireNotNull(targetEmptyFiles)
                    //开始压缩
                    val resultPhotos = createCompressBuilder(context, targetFiles, targetPath, custom).get().firstOrNull()
                    Log.i(TAG, "压缩大小 = ${FileUtils.getSize(resultPhotos)}")
                    //删除AndroidQ刚复制的沙盒文件
                    clearCacheFiles(targetFiles)
                    return resultPhotos
                }

                override fun onSuccess(resultPhotos: File?) {
                    result.postValue(resultPhotos)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            result.postValue(null)
        }
        return result
    }

    /**
     * 批量压缩
     *
     * @param uris 原图片集的地址uri
     * @param targetPath 生成图片的地址
     * @param custom 自定义压缩的回调
     */
    fun compress(
        context: Context, uris: List<Uri>, targetPath: String, custom: ((builder: Luban.Builder) -> Unit)? = null
    ): MutableLiveData<List<File>> {
        val result = MutableLiveData<List<File>>()
        try {
            ThreadUtils.executeByIo(object : ThreadUtils.SimpleTask<MutableList<File>>() {
                override fun doInBackground(): MutableList<File> {
                    val targetEmptyFiles = transformUri2File(context, uris)                                     //Uri转Files （可能存在失败）
                    val targetFiles = requireNotNull(targetEmptyFiles)                                          //遍历判断是否有转换失败的情况
                    val resultPhotos = createCompressBuilder(context, targetFiles, targetPath, custom).get()    //开始压缩
                    clearCacheFiles(targetFiles)                                                                //删除AndroidQ刚复制的沙盒文件
                    return resultPhotos
                }

                override fun onSuccess(resultPhotos: MutableList<File>) {
                    result.postValue(resultPhotos)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            result.postValue(emptyList())
        }
        return result
    }

    /** 创建统一压缩格式 */
    private fun createCompressBuilder(
        context: Context?, files: List<File>, targetPath: String, custom: ((builder: Luban.Builder) -> Unit)? = null
    ): Luban.Builder {
        return Luban.with(context).load(files)                 //传入原图
            .ignoreBy(50)           //不压缩的阈值，单位为K
            .setTargetDir(targetPath)   //缓存压缩图片路径
            .filter { path ->
                !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault()).endsWith(".gif"))
            } //设置开启压缩条件
            .also { custom?.invoke(it) }
    }

    /** 将Uri转换成File */
    private fun transformUri2File(context: Context?, uris: List<Uri>): List<File?> {
        return uris.map { copyUri2FileQ(context, it) }
    }

    /** 断言文件列表的空值 */
    private fun requireNotNull(files: List<File?>): List<File> {
        if (files.any { it == null }) throw RuntimeException("文件复制至沙盒失败")
        return files.mapNotNull { it }
    }

    /** Android Q 以上的沙盒文件复制 */
    private fun copyUri2FileQ(context: Context?, uri: Uri): File? = when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> File(requireNotNull(uri.path))
        ContentResolver.SCHEME_CONTENT -> {
            //把文件保存到沙盒
            val contentResolver = context?.contentResolver
            val displayName = "${System.currentTimeMillis()}${Random().nextInt(Int.MAX_VALUE)}.${MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver?.getType(uri))}"
            val ios = contentResolver?.openInputStream(uri)
            if (ios == null) {
                null
            } else {
                val targetFile = File("${HCPath.IMAGE_CACHE}/$displayName")
                FileIOUtils.writeFileFromIS(targetFile, ios)
                targetFile
            }
        }

        else -> null
    }

    /** 清除沙盒缓存文件 */
    private fun clearCacheFiles(files: List<File>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        files.forEach { FileUtils.delete(it) }
    }

}