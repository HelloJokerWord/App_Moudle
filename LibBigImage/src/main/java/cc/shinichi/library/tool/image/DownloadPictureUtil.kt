package cc.shinichi.library.tool.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentActivity
import cc.shinichi.library.ImagePreview
import cc.shinichi.library.R
import cc.shinichi.library.glide.FileTarget
import cc.shinichi.library.tool.ui.ToastUtil
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ThreadUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import java.io.File

/**
 * @author 工藤
 * @email qinglingou@gmail.com
 * create at 2018/5/4  16:34
 * description:图片下载工具类
 */
object DownloadPictureUtil {

    fun downloadPicture(context: FragmentActivity, currentItem: Int, url: String?) {
        Glide.with(context).downloadOnly().load(url).into(object : FileTarget() {
            override fun onLoadStarted(placeholder: Drawable?) {
                super.onLoadStarted(placeholder)
                if (ImagePreview.instance.downloadListener != null) {
                    ImagePreview.instance.downloadListener?.onDownloadStart(context, currentItem)
                } else {
                    ToastUtil.instance.showShort(context, context.getString(R.string.toast_start_download))
                }
                super.onLoadStarted(placeholder)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                if (ImagePreview.instance.downloadListener != null) {
                    ImagePreview.instance.downloadListener?.onDownloadFailed(context, currentItem)
                } else {
                    ToastUtil.instance.showShort(context, context.getString(R.string.toast_save_failed))
                }
            }

            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                super.onResourceReady(resource, transition)
                save(context, resource, currentItem)
            }
        })
    }

    private fun save(context: FragmentActivity, resource: File, currentItem: Int) {
        ThreadUtils.getIoPool().execute {
            val bmp = ImageUtils.getBitmap(resource)
            if (bmp != null) {
                ImageUtils.save2Album(bmp, Bitmap.CompressFormat.PNG)
                ThreadUtils.runOnUiThread {
                    if (ImagePreview.instance.downloadListener != null) {
                        ImagePreview.instance.downloadListener?.onDownloadSuccess(context, currentItem)
                    } else {
                        ToastUtil.instance.showShort(context, context.getString(R.string.big_img_save_success))
                    }
                }
            } else {
                ThreadUtils.runOnUiThread {
                    if (ImagePreview.instance.downloadListener != null) {
                        ImagePreview.instance.downloadListener?.onDownloadFailed(context, currentItem)
                    } else {
                        ToastUtil.instance.showShort(context, context.getString(R.string.toast_save_failed))
                    }
                }
            }
        }
    }
}