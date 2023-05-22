package com.example.appmoudle.manager.photo

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.appmoudle.R
import com.luck.lib.camerax.SimpleCameraX
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectLimitType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnCameraInterceptListener
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.PictureFileUtils
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.luck.picture.lib.utils.StyleUtils
import com.third.libcommon.PermissionManager
import com.third.libcommon.constant.GlobalPath
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File


/**
 * 相册图片选择器
 *
 * 详细参数配置介绍：
 * https://github.com/LuckSiege/PictureSelector/blob/version_component/README_CN.md
 */
object PhotosSelectManager {

    private const val TAG = "PhotosSelectManager"

    private val selectorStyle = PictureSelectorStyle()

    var selectResult: ((data: MutableList<PhotoBean>) -> Unit)? = null

    private val callResult by lazy {
        object : OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: ArrayList<LocalMedia>?) {
                if (result.isNullOrEmpty()) {
                    selectResult?.invoke(mutableListOf())
                    return
                }
                try {
                    val pathList = mutableListOf<PhotoBean>()
                    result.forEach { media ->
                        if (media.width == 0 || media.height == 0) {
                            when {
                                PictureMimeType.isHasImage(media.mimeType) -> {
                                    val imageExtraInfo = MediaUtils.getImageSize(Utils.getApp(), media.cutPath)
                                    media.width = imageExtraInfo.width
                                    media.height = imageExtraInfo.height
                                }

                                PictureMimeType.isHasVideo(media.mimeType) -> {
                                    val videoExtraInfo = MediaUtils.getVideoSize(Utils.getApp(), media.path)
                                    media.width = videoExtraInfo.width
                                    media.height = videoExtraInfo.height
                                }
                            }
                        }

                        Log.i(
                            TAG, "\n文件名: ${media.fileName} " +
                                    "是否压缩:${media.isCompressed} " +
                                    "压缩:${media.compressPath} " +
                                    "初始路径:${media.path} " +
                                    "绝对路径:${media.realPath} " +
                                    "是否裁剪:${media.isCut}\n " +
                                    "裁剪路径:${media.cutPath} " +
                                    "是否开启原图:${media.isOriginal} " +
                                    "原图路径:${media.originalPath} " +
                                    "沙盒路径:${media.sandboxPath} " +
                                    "水印路径:${media.watermarkPath} " +
                                    "视频缩略图:${media.videoThumbnailPath}\n" +
                                    "原始宽高:${media.width} x ${media.height} " +
                                    "裁剪宽高:${media.cropImageWidth} x ${media.cropImageHeight} " +
                                    "文件大小:${PictureFileUtils.formatAccurateUnitFileSize(media.size)} " +
                                    "文件时长:${media.duration} "
                        )

                        pathList.add(PhotoBean(media.cutPath, File(media.cutPath), media.width, media.height))
                    }
                    if (pathList.isEmpty()) {
                        ToastUtils.showShort(R.string.hc_common_error_tips)
                        return
                    }
                    selectResult?.invoke(pathList)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtils.showShort(R.string.hc_common_error_tips)
                }
            }

            override fun onCancel() {
                //ToastUtils.showShort(R.string.hc_cancel)
            }
        }
    }

    /**
     * 打开相机拍照获取
     */
    fun openCamera(activity: FragmentActivity?, result: (data: MutableList<PhotoBean>) -> Unit) {
        if (activity == null) return
        PermissionManager.checkCameraPermission(activity, mOnGranted = {
            selectResult = result

            PictureSelector.create(activity)
                .openCamera(SelectMimeType.ofImage())
                .setCameraInterceptListener(MeOnCameraInterceptListener())
                .setCropEngine(ImageFileCropEngine())
                .setCompressEngine(ImageFileCompressEngine())
                .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
                //.setCustomLoadingListener(getCustomLoadingListener())
                .setDefaultLanguage(LanguageConfig.TRADITIONAL_CHINESE)
                .setSandboxFileEngine(MeSandboxFileEngine())
                .isOriginalControl(true)
                .setPermissionDescriptionListener(MeOnPermissionDescriptionListener())
                .forResult(callResult)
        })
    }

    /**
     * 打开相册获取
     */
    fun openAlbum(activity: FragmentActivity?, result: (data: MutableList<PhotoBean>) -> Unit) {
        if (activity == null) return
        PermissionManager.checkPhotoPermission(activity, mOnGranted = {
            selectResult = result

            PictureSelector.create(activity)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(selectorStyle)
                .setImageEngine(PhotoSelectGlideEngine.createGlideEngine())
                .setCropEngine(ImageFileCropEngine())
                .setCompressEngine(ImageFileCompressEngine())
                .setSandboxFileEngine(MeSandboxFileEngine())
                .setCameraInterceptListener(MeOnCameraInterceptListener())
                .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
                //.setEditMediaInterceptListener(getCustomEditMediaEvent())
                .setPermissionDescriptionListener(MeOnPermissionDescriptionListener())
                //.setPreviewInterceptListener(getPreviewInterceptListener())
                .setPermissionDeniedListener(MeOnPermissionDeniedListener())
                .isPageSyncAlbumCount(true)
                //.setCustomLoadingListener(getCustomLoadingListener())
                .setQueryFilterListener { false }
                .setSelectionMode(SelectModeConfig.SINGLE)
                .setDefaultLanguage(LanguageConfig.TRADITIONAL_CHINESE)
                .setQuerySortOrder(MediaStore.MediaColumns.DATE_MODIFIED + " ASC")
                .setOutputCameraDir(GlobalPath.CAMERA_OUT_PUT)
                .setQuerySandboxDir(GlobalPath.CAMERA_OUT_PUT)
                .isDisplayTimeAxis(false)
                //.isOnlyObtainSandboxDir(cb_only_dir.isChecked())
                .isPageStrategy(true)
                .isOriginalControl(true)
                .isDisplayCamera(true)
                .setSkipCropMimeType(PictureMimeType.ofGIF(), PictureMimeType.ofWEBP())
                .isFastSlidingSelect(false)
                .isWithSelectVideoImage(false)
                .isPreviewFullScreenMode(true)
                .isPreviewZoomEffect(false)
                .isPreviewImage(true)
                .isMaxSelectEnabledMask(false)
                .isDirectReturnSingle(false)
                .setMaxSelectNum(1)
                .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION)
                .isGif(false)
                .forResult(callResult)
        })
    }

    /**
     * 自定义拍照
     */
    private class MeOnCameraInterceptListener : OnCameraInterceptListener {
        override fun openCamera(fragment: Fragment?, cameraMode: Int, requestCode: Int) {
            SimpleCameraX.of().apply {
                isAutoRotation(true)
                setCameraMode(cameraMode)
                setVideoFrameRate(25)
                setVideoBitRate(3 * 1024 * 1024)
                //isDisplayRecordChangeTime(true)
                isManualFocusCameraPreview(true)
                isZoomCameraPreview(true)
                setOutputPathDir(GlobalPath.CAMERA_OUT_PUT)
                setImageEngine { context, url, imageView -> Glide.with(context).load(url).into(imageView) }
                fragment?.activity?.let { start(it, fragment, requestCode) }
            }
        }
    }

    /**
     * 自定义裁剪
     */
    private class ImageFileCropEngine : CropFileEngine {

        override fun onStartCrop(fragment: Fragment, srcUri: Uri, destinationUri: Uri, dataSource: ArrayList<String>, requestCode: Int) {
            val options = UCrop.Options().apply {
                setHideBottomControls(false)
                setFreeStyleCropEnabled(false)
                setShowCropFrame(true)
                setShowCropGrid(true)
                setCircleDimmedLayer(false)
                withAspectRatio(-1f, -1f)
                setCropOutputPathDir(GlobalPath.CROP_OUT_PUT)
                isCropDragSmoothToCenter(false)
                setSkipCropMimeType(PictureMimeType.ofGIF(), PictureMimeType.ofWEBP())
                isForbidCropGifWebp(true)
                isForbidSkipMultipleCrop(true)
                setMaxScaleMultiplier(100f)
                if (selectorStyle.selectMainStyle.statusBarColor != 0) {
                    val mainStyle: SelectMainStyle = selectorStyle.selectMainStyle
                    val isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack
                    val statusBarColor = mainStyle.statusBarColor
                    isDarkStatusBarBlack(isDarkStatusBarBlack)
                    if (StyleUtils.checkStyleValidity(statusBarColor)) {
                        setStatusBarColor(statusBarColor)
                        setToolbarColor(statusBarColor)
                    } else {
                        setStatusBarColor(ColorUtils.getColor(R.color.ps_color_grey))
                        setToolbarColor(ColorUtils.getColor(R.color.ps_color_grey))
                    }
                    val titleBarStyle: TitleBarStyle = selectorStyle.titleBarStyle
                    if (StyleUtils.checkStyleValidity(titleBarStyle.titleTextColor)) {
                        setToolbarWidgetColor(titleBarStyle.titleTextColor)
                    } else {
                        setToolbarWidgetColor(ColorUtils.getColor(R.color.color_white))
                    }
                } else {
                    setStatusBarColor(ColorUtils.getColor(R.color.ps_color_grey))
                    setToolbarColor(ColorUtils.getColor(R.color.ps_color_grey))
                    setToolbarWidgetColor(ColorUtils.getColor(R.color.color_white))
                }
            }

            val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine {
                override fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
                    if (!assertValidRequest(context) || context == null || imageView == null) return
                    Glide.with(context)
                        .load(url)
                        .override(180, 180)
                        .into(imageView)
                }

                override fun loadImage(context: Context?, url: Uri?, maxWidth: Int, maxHeight: Int, call: UCropImageEngine.OnCallbackListener<Bitmap>?) {
                    if (context == null || url == null) return
                    Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .override(maxWidth, maxHeight)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                call?.onCall(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                call?.onCall(null)
                            }
                        })
                }
            })
            uCrop.start(fragment.requireActivity(), fragment, requestCode)
        }

        private fun assertValidRequest(context: Context?) = when {
            context is Activity -> !isDestroy(context)
            context is ContextWrapper && (context.baseContext is Activity) -> !isDestroy(context.baseContext as Activity)
            else -> true
        }

        private fun isDestroy(activity: Activity?) = if (activity == null) true else activity.isFinishing || activity.isDestroyed
    }

    /**
     * 自定义压缩
     */
    private class ImageFileCompressEngine : CompressFileEngine {
        override fun onStartCompress(context: Context?, source: ArrayList<Uri>?, call: OnKeyValueResultCallbackListener?) {
            if (context == null || source == null || call == null) return
            Luban.with(context)
                .load(source)
                .ignoreBy(100)
                .setRenameListener { filePath ->
                    val indexOf = filePath.lastIndexOf(".")
                    val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                    DateUtils.getCreateFileName("CMP_") + postfix
                }.filter { path ->
                    if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) true else !PictureMimeType.isUrlHasGif(path)
                }.setCompressListener(object : OnCompressListener {
                    override fun onStart() {}
                    override fun onSuccess(file: File?) {
                        call.onCallback("", file?.path)
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        call.onCallback("", null)
                    }
                }).launch()
        }
    }

    /**
     * 拦截自定义提示
     */
    private class MeOnSelectLimitTipsListener : OnSelectLimitTipsListener {
        override fun onSelectLimitTips(context: Context?, media: LocalMedia?, config: SelectorConfig?, limitType: Int): Boolean {
            if (context == null || media == null || config == null) return false
            when (limitType) {
                SelectLimitType.SELECT_MIN_SELECT_LIMIT -> {
                    ToastUtils.showShort("图片最少不能低于" + config.minSelectNum + "张")
                    return true
                }

                SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT -> {
                    ToastUtils.showShort("视频最少不能低于" + config.minVideoSelectNum + "个")
                    return true
                }

                SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT -> {
                    ToastUtils.showShort("音频最少不能低于" + config.minAudioSelectNum + "个")
                    return true
                }
            }
            return false
        }
    }

    /**
     * 自定义沙盒文件处理
     */
    private class MeSandboxFileEngine : UriToFileTransformEngine {
        override fun onUriToFileAsyncTransform(context: Context?, srcPath: String?, mineType: String?, call: OnKeyValueResultCallbackListener?) {
            call?.onCallback(srcPath, SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType))
        }
    }

    /**
     * 添加权限说明
     */
    private class MeOnPermissionDescriptionListener : OnPermissionDescriptionListener {
        override fun onPermissionDescription(fragment: Fragment?, permIkionArray: Array<out String>?) {
            Log.i(TAG, "onPermissionDescription array=$permIkionArray")
        }

        override fun onDismiss(fragment: Fragment?) {
            Log.i(TAG, "onDismiss")
        }
    }

    /**
     * 权限拒绝后回调
     */
    private class MeOnPermissionDeniedListener : OnPermissionDeniedListener {
        override fun onDenied(fragment: Fragment?, permissionArray: Array<out String>?, requestCode: Int, call: OnCallbackListener<Boolean>?) {
            Log.i(TAG, "permissionArray=$permissionArray")
        }

    }
}