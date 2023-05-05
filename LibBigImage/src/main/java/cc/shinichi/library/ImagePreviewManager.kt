package cc.shinichi.library

import android.content.Context
import cc.shinichi.library.bean.ImageInfo


/**
 * 图片浏览器管理器
 */
object ImagePreviewManager {

    /**
     * @param imageUrl 图片链接
     * @param custom 自定义图片浏览器参数
     */
    fun toView(context: Context?, imageUrl: String, custom: ((ip: ImagePreview) -> Unit)? = null) {
        toView(context, arrayListOf(imageUrl), 0, custom)
    }

    /**
     * @param imageUrls 批量图片链接
     * @param custom 自定义图片浏览器参数
     */
    fun toView(context: Context?, imageUrls: MutableList<String>, position: Int, custom: ((ip: ImagePreview) -> Unit)? = null) {
        if (imageUrls.isEmpty()) return
        createDefaultPreview(context)?.apply {
            setImageList(imageUrls)
                .setIndex(position)
                .also { custom?.invoke(it) }
                .start()
        }
    }

    /**
     * @param imageInfo 批量图片链接
     * @param custom 自定义图片浏览器参数
     */
    fun toViewWithPreview(context: Context?, imageInfo: MutableList<ImageInfo>, position: Int, custom: ((ip: ImagePreview) -> Unit)? = null) {
        createDefaultPreview(context)?.apply {
            setImageInfoList(imageInfo)
                .setIndex(position)
                .also { custom?.invoke(it) }
                .start()
        }
    }

    private fun createDefaultPreview(context: Context?): ImagePreview? {
        if (context == null) return null
        return ImagePreview.instance
            .setContext(context)
            .setShowDownButton(false)
    }

}