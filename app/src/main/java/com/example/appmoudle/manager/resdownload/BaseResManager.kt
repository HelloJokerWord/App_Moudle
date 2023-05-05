package com.example.appmoudle.manager.resdownload

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.manager.GlideManager
import com.third.libcommon.MMKVManager
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack
import com.third.libcommon.http.URLApi
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created on 2023/2/17.
 * @author Joker
 * Des:
 */

abstract class BaseResManager {

    /**
     * 资源类型
     */
    abstract fun getResType(): String

    /**
     * 请求资源类型
     */
    abstract fun resScenes(): Int

    /**
     * 资源文件路径
     */
    abstract fun getResPath(resUrl: String?): String

    /**
     * 资源文件
     */
    abstract fun getResFile(resUrl: String?): File

    /**
     * 资源名字
     */
    abstract fun getFileName(resUrl: String?): String

    abstract fun getRootPath(): String

    companion object {
        private const val DOWN_SIZE = 5  //并发下载大小

        const val RES_TYPE_AVATAR_FRAME = 1  //头像框
    }

    private val linkedBlockingQueue by lazy { LinkedBlockingQueue<ResItemEntity>() }

    @Volatile
    private var downSize = 0

    /**
     * 请求资源列表
     */
    fun reqFileRes(owner: LifecycleOwner) {
        HttpManager.get(owner, URLApi.URL_RES_FILE, mutableMapOf("res_scenes" to resScenes()), object : RequestCallBack<ResListEntity?> {
            override fun onSuccess(data: ResListEntity?) {
                if (data != null && !data.list.isNullOrEmpty()) {
                    val lastVersion = MMKVManager.getInt(getResType(), -1)
                    if (lastVersion >= data.version) return
                    MMKVManager.put(getResType(), data.version)

                    ThreadUtils.getIoPool().execute {
                        FileUtils.deleteAllInDir(getRootPath())
                        linkedBlockingQueue.addAll(data.list)
                        handleQueue(owner)
                    }
                }

            }

            override fun onFail(code: Int, msg: String?) {
                Log.e(getResType(), "未知文件类型 $code / $msg /${getResType()}")
            }
        })
    }

    fun handleQueue(owner: LifecycleOwner) {
        if (linkedBlockingQueue.isEmpty()) {
            downSize = 0
            return
        }

        val res = linkedBlockingQueue.poll()
        if (res == null) {
            handleQueue(owner)
            return
        }

        Log.d(getResType(), "开始预加载资源 type=${res.res_type}  url=${res.res_url}")
        when (res.res_type) {
            ResItemEntity.TYPE_SVGA -> {
                downLoadFileRes(owner, res.res_url)
            }

            ResItemEntity.TYPE_IMG -> {
                GlideManager.preloadImage(Utils.getApp(), res.res_url)
                handleQueue(owner)
            }

            else -> {
                Log.e(getResType(), "未知文件类型 ${res.res_url} /${res.res_type}")
                handleQueue(owner)
            }
        }
    }

    /**
     * 下载资源
     */
    private fun downLoadFileRes(owner: LifecycleOwner, url: String?) {
        if (url.isNullOrEmpty() || FileUtils.isFileExists(getResPath(url))) {
            handleQueue(owner)
            return
        }

        if (downSize <= DOWN_SIZE) {
            downSize++
            handleQueue(owner)
        }

        HttpManager.downloadFile(owner, url, getResPath(url), reqResult = object : RequestCallBack<String> {

            override fun onSuccess(data: String) {
                Log.d(getResType(), "下载成功, url=$url")
                downSize--
                handleQueue(owner)
            }

            override fun onFail(code: Int, msg: String?) {
                Log.e(getResType(), "下载失败 $code / $msg / $url")
                downSize--
                handleQueue(owner)
            }
        })
    }

    /**
     * 单个下载
     */
    private fun offerDownLoadFileRes(owner: LifecycleOwner, url: String?) {
        val newRes = ResItemEntity(res_url = url, res_type = ResItemEntity.TYPE_SVGA)
        linkedBlockingQueue.offer(newRes)
        handleQueue(owner)
    }

    /**
     * 重新下载 可能损坏
     */
    fun reDownload(owner: LifecycleOwner, url: String?) {
        if (url.isNullOrEmpty()) return
        ThreadUtils.getIoPool().execute {
            val oriPath = getResPath(url)
            FileUtils.delete(oriPath)
            offerDownLoadFileRes(owner, url)
        }
    }
}