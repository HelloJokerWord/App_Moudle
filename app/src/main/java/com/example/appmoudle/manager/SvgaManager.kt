package com.example.appmoudle.manager

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileUtils
import com.example.appmoudle.BuildConfig
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGASoundManager
import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.utils.log.SVGALogger
import java.io.FileInputStream
import java.net.URL
import java.util.concurrent.ConcurrentHashMap


/**
 * svga播放器
 */
object SvgaManager {

    private val TAG = SvgaManager::class.java.simpleName
    private val mapSVGAVideoEntity = ConcurrentHashMap<String, SVGAVideoEntity>()

    /**
     * 初始化svga
     */
    fun initSvga(context: Context) {
        SVGAParser.shareParser().init(context)
        SVGASoundManager.init()
        SVGALogger.setLogEnabled(BuildConfig.DEBUG)
    }

    fun startRefresh(svgaImageView: SVGAImageView?) {
        decodeFromAssets(svgaImageView, "svga/common_loading_refresh.svga")
    }

    fun startPageLoading(svgaImageView: SVGAImageView?) {
        decodeFromAssets(svgaImageView, "svga/common_page_loading.svga")
    }

    /**
     * 播放svga动画，view，assets指定目录的svga文件，动态变更实体类
     *
     * 注意生命周期结束时，关闭播放
     */
    private fun decodeFromAssets(svgaImageView: SVGAImageView?, svgaFileName: String?) {
        if (svgaImageView == null || svgaFileName == null) {
            Log.e(TAG, "svgaFileName = null $svgaFileName")
            return
        }
        try {
            stopPlay(svgaImageView)
            if (mapSVGAVideoEntity.containsKey(svgaFileName)) {
                mapSVGAVideoEntity[svgaFileName]?.apply {
                    startPlay(svgaImageView, this)
                }
            } else {
                SVGAParser.shareParser().decodeFromAssets(svgaFileName, object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        mapSVGAVideoEntity[svgaFileName] = videoItem
                        startPlay(svgaImageView, videoItem)
                    }

                    override fun onError() {
                        Log.e(TAG, "play error from assert")
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun decodeFromFile(svgaImageView: SVGAImageView?, filePath: String?, cacheKey: String, onPlayError: ((code: Int, msg: String) -> Unit)? = null) {
        if (filePath.isNullOrEmpty() || !FileUtils.isFileExists(filePath) || svgaImageView == null) {
            onPlayError?.invoke(-1, "filePath = null $filePath")
            Log.e(TAG, "filePath = null $filePath")
            return
        }
        try {
            stopPlay(svgaImageView)
            if (mapSVGAVideoEntity.containsKey(cacheKey)) {
                mapSVGAVideoEntity[cacheKey]?.apply {
                    startPlay(svgaImageView, this)
                }
            } else {
                SVGAParser.shareParser().decodeFromInputStream(FileInputStream(filePath), cacheKey, object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        mapSVGAVideoEntity[cacheKey] = videoItem
                        startPlay(svgaImageView, videoItem)
                    }

                    override fun onError() {
                        onPlayError?.invoke(-1, "文件错误")
                        Log.e(TAG, "onError $filePath")
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 加载网络SVGA
     *
     * 注意生命周期结束时，关闭播放
     */
    fun decodeFromURL(svgaImageView: SVGAImageView?, url: String?, onPlayError: ((code: Int, msg: String) -> Unit)? = null) {
        if (svgaImageView == null || url.isNullOrEmpty()) {
            onPlayError?.invoke(-1, "svgaImageView 或 url 为空")
            Log.e(TAG, "play error from url")
            return
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
                stopPlay(svgaImageView)
                val cacheKey = EncryptUtils.encryptMD5ToString(url)
                if (mapSVGAVideoEntity.containsKey(cacheKey)) {
                    mapSVGAVideoEntity[cacheKey]?.apply {
                        startPlay(svgaImageView, this)
                    }
                } else {
                    SVGAParser.shareParser().decodeFromURL(URL(url), object : SVGAParser.ParseCompletion {
                        override fun onComplete(videoItem: SVGAVideoEntity) {
                            mapSVGAVideoEntity[cacheKey] = videoItem
                            startPlay(svgaImageView, videoItem)
                        }

                        override fun onError() {
                            Log.e(TAG, "play error from url")
                            onPlayError?.invoke(-1, "播放错误")
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.e(TAG, "error url")
            onPlayError?.invoke(-1, "url错误")
        }
    }

    private fun startPlay(svgaImageView: SVGAImageView, videoItem: SVGAVideoEntity) {
        svgaImageView.isVisible = true
        svgaImageView.setImageDrawable(SVGADrawable(videoItem))
        svgaImageView.startAnimation()
    }

    /**
     * 停止播放
     */
    fun stopPlay(svgaImageView: SVGAImageView?) {
        svgaImageView?.apply {
            callback = null
            stopAnimation(true)
            visibility = View.GONE
        }
    }
}