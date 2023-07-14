package com.example.appmoudle.manager

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.appmoudle.R
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation
import jp.wasabeef.glide.transformations.GrayscaleTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation.CornerType
import java.io.File

/**
 * Glide工具类
 */
object GlideManager {

    /*** 占位图  */
    private var placeholderImage = R.drawable.hc_image_placeholder

    /*** 错误图  */
    private var errorImage = R.drawable.hc_image_placeholder

    /**
     * 加载图片(默认)
     *
     * @param context   上下文
     * @param url       链接 或 本地文件路径 或 asset路径
     * @param imageView ImageView
     */
    fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions()
            .placeholder(placeholderImage)   //占位图
            .error(errorImage)               //错误图
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 加载本地路径文件
     * @param path 文件路径
     */
    fun loadImageLocal(context: Context?, path: String?, imageView: ImageView?) {
        if (context == null || !FileUtils.isFileExists(path) || imageView == null) return

        val options = RequestOptions()
            .placeholder(placeholderImage)   //占位图
            .error(errorImage)               //错误图
        Glide.with(context).load(path).apply(options).into(imageView)
    }

    /**
     * 加载本地路径文件
     * @param file 文件
     */
    fun loadImageFile(context: Context?, file: File?, imageView: ImageView?) {
        if (context == null || !FileUtils.isFileExists(file) || imageView == null) return

        val options = RequestOptions()
            .placeholder(placeholderImage)   //占位图
            .error(errorImage)               //错误图
        Glide.with(context).load(file).apply(options).into(imageView)
    }

    /**
     * 加载本地路径文件
     * @param drawableId 资源id
     */
    fun loadImageDrawableId(context: Context?, @DrawableRes drawableId: Int, imageView: ImageView?) {
        if (context == null || drawableId == 0 || imageView == null) return

        val options = RequestOptions()
            .placeholder(placeholderImage)   //占位图
            .error(errorImage)               //错误图
        Glide.with(context).load(drawableId).apply(options).into(imageView)
    }

    /**
     * 指定图片大小;使用override()方法指定了一个图片的尺寸。
     * Glide现在只会将图片加载成width*height像素的尺寸，而不会管你的ImageView的大小是多少了。
     * 如果你想加载一张图片的原始尺寸的话，可以使用Target.SIZE_ORIGINAL关键字----override(Target.SIZE_ORIGINAL)
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     * @param width     图片宽度
     * @param height    图片高度
     */
    fun loadImageSize(context: Context?, url: String?, imageView: ImageView?, width: Int, height: Int) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions()
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图
            .override(width, height)
            .priority(Priority.HIGH)
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 禁用内存缓存功能
     * diskCacheStrategy()方法基本上就是Glide硬盘缓存功能的一切，它可以接收五种参数：
     *
     *
     * DiskCacheStrategy.NONE： 表示不缓存任何内容。
     * DiskCacheStrategy.DATA： 表示只缓存原始图片。
     * DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片。
     * DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
     * DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）。
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     */
    fun loadImageSizeKipMemoryCache(context: Context?, url: String?, imageView: ImageView?) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions()
            .placeholder(placeholderImage)   //占位图
            .error(errorImage)               //错误图
            .skipMemoryCache(true)      //禁用掉Glide的内存缓存功能
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 预先加载图片
     * 在使用图片之前，预先把图片加载到缓存，调用了预加载之后，我们以后想再去加载这张图片就会非常快了，
     * 因为Glide会直接从缓存当中去读取图片并显示出来
     *
     * @param context 上下文
     * @param url     链接
     */
    fun preloadImage(context: Context?, url: String?) {
        if (context == null || url.isNullOrEmpty()) return
        Glide.with(context).load(url).preload()
    }

    /**
     * 加载圆形图片
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     */
    fun loadCircleImage(context: Context?, url: String?, imageView: ImageView?) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions()
            .centerCrop()
            .circleCrop() //设置圆形
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图
            .priority(Priority.HIGH)
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 加载圆形带边框图片
     *
     * @param context     上下文
     * @param url         链接
     * @param imageView   ImageView
     * @param borderSize  边框宽度 px
     * @param borderColor 边框颜色
     */
    fun loadCircleWithBorderImage(context: Context?, url: String?, imageView: ImageView?, borderSize: Float, @ColorInt borderColor: Int) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions.bitmapTransform(
            MultiTransformation(
                CenterCrop(),
                CropCircleWithBorderTransformation(SizeUtils.dp2px(borderSize), borderColor)
            )
        )
            .placeholder(placeholderImage)  //占位图
            .error(errorImage)              //错误图
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 加载圆角图片
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     * @param radius    圆角 px
     */
    fun loadRoundCircleImage(context: Context?, url: String?, imageView: ImageView?, radius: Float, @DrawableRes placeholder: Int = placeholderImage, @DrawableRes error: Int = errorImage) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions.bitmapTransform(
            MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(SizeUtils.dp2px(radius), 0, CornerType.ALL)
            )
        )
            .placeholder(placeholder) //占位图
            .error(error) //错误图
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 加载圆角图片
     *
     * @param context   上下文
     * @param path       文件路径
     * @param imageView ImageView
     * @param radius    圆角 px
     */
    fun loadRoundCircleImageLocal(context: Context?, path: String?, imageView: ImageView?, radius: Float) {
        if (context == null || !FileUtils.isFileExists(path) || imageView == null) return

        val options = RequestOptions.bitmapTransform(
            MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(SizeUtils.dp2px(radius), 0, CornerType.ALL)
            )
        )
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图
        Glide.with(context).load(path).apply(options).into(imageView)
    }

    /**
     * 加载圆角图片-指定任意部分圆角（图片上、下、左、右四个角度任意定义）
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     * @param radius    圆角 px
     * @param type      圆角位置
     */
    fun loadRoundCircleImage(context: Context?, url: String?, imageView: ImageView?, radius: Float, type: CornerType?) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions.bitmapTransform(
            MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(SizeUtils.dp2px(radius), 0, type)
            )
        )
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 加载模糊图片（自定义透明度）
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     * @param blur      模糊度，一般1-100够了，越大越模糊
     */
    fun loadBlurImage(context: Context?, url: String?, imageView: ImageView?, blur: Int) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions.bitmapTransform(
            MultiTransformation(
                CenterCrop(),
                BlurTransformation(blur)
            )
        )
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 加载模糊图片（自定义透明度）
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     * @param blur      模糊度，一般1-100够了，越大越模糊
     * @param sampling  取样
     */
    fun loadBlurImage(context: Context?, url: String?, imageView: ImageView?, blur: Int, sampling: Int) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions.bitmapTransform(
            MultiTransformation(
                CenterCrop(),
                BlurTransformation(blur, sampling)
            )
        )
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 加载灰度(黑白)图片（自定义透明度）
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     */
    fun loadBlackImage(context: Context?, url: String?, imageView: ImageView?) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions.bitmapTransform(
            MultiTransformation(
                CenterCrop(),
                GrayscaleTransformation()
            )
        )
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * Glide.with(this).asGif()    //强制指定加载动态图片
     * 如果加载的图片不是gif，则asGif()会报错， 当然，asGif()不写也是可以正常加载的。
     * 加入了一个asBitmap()方法，这个方法的意思就是说这里只允许加载静态图片，不需要Glide去帮我们自动进行图片格式的判断了。
     * 如果你传入的还是一张GIF图的话，Glide会展示这张GIF图的第一帧，而不会去播放它。
     *
     * @param context   上下文
     * @param url       链接
     * @param imageView ImageView
     */
    private fun loadGif(context: Context?, url: String?, imageView: ImageView?) {
        if (context == null || url.isNullOrEmpty() || imageView == null) return

        val options = RequestOptions()
            .placeholder(placeholderImage) //占位图
            .error(errorImage) //错误图

        Glide.with(context)
            .load(url)
            .apply(options)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean) = false
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean) = false
            })
            .into(imageView)
    }

    /**
     * 下载图片
     * 在RequestListener的onResourceReady方法里面获取下载File图片
     * new RequestListener<File>() {
     * *@Override
     * public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
     * return false;
     * }
     *
     *
     * *@Override
     * public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
     * //resource即为下载取得的图片File
     * return false;
     * }
     * }
     *
     * @param context         上下文
     * @param url             下载链接
     * @param result          下载结果
    </File></File></File> */
    fun downloadImage(context: Context?, url: String?, result: (file: File?) -> Unit) {
        if (context == null || url.isNullOrEmpty()) {
            result.invoke(null)
            return
        }

        Glide.with(context)
            .downloadOnly()
            .load(url)
            .addListener(object : RequestListener<File?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File?>?, isFirstResource: Boolean): Boolean {
                    e?.printStackTrace()
                    result.invoke(null)
                    return false
                }

                override fun onResourceReady(resource: File?, model: Any?, target: Target<File?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    result.invoke(resource)
                    return false
                }
            })
            .preload()
    }

}