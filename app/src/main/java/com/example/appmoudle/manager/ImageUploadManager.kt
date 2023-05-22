package com.example.appmoudle.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.FileUtils
import com.third.libcommon.constant.GlobalPath
import com.example.appmoudle.manager.photo.PhotosManager
import com.third.libcommon.LubanManager
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack
import com.third.libcommon.http.URLApi
import java.io.File

/**
 * CreateBy:Joker
 * CreateTime:2023/5/4 18:33
 * description：
 */
object ImageUploadManager {

    /**
     * 将原图压缩上传
     */
    fun uploadImage(fragment: Fragment, oriImagePath: String) {
        LubanManager.compress(fragment.context, oriImagePath, GlobalPath.getImageCompressPath()) { file ->
            if (FileUtils.isFileExists(file) && file != null) {
                uploadImg(fragment.viewLifecycleOwner, file)
            }
        }
    }

    /**
     * 选择本地图片上传
     */
    fun selectImageAndUpload(activity: FragmentActivity, isTakePhoto: Boolean = false) {
        if (isTakePhoto) {
            PhotosManager.openCamera(activity) {
                it.forEach { bean -> uploadImg(activity, bean.file) }
            }
        } else {
            PhotosManager.openAlbum(activity) {
                it.forEach { bean -> uploadImg(activity, bean.file) }
            }
        }
    }

    private fun uploadImg(owner: LifecycleOwner, file: File) {
        HttpManager.uploadFile(owner, URLApi.UPLOAD_URL, file, reqResult = object : RequestCallBack<String> {
            override fun onSuccess(data: String) {

            }

            override fun onFail(code: Int, msg: String?) {
            }
        })
    }

}