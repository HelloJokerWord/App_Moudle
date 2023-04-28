package com.example.appmoudle.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.StringUtils
import com.example.appmoudle.R
import com.example.appmoudle.dialog.AppConfirmPopup

/**
 * 作者: Joker
 * 时间: 2019/2/21. 11:34
 * 描述:
 */
@SuppressLint("WrongConstant")
object PermissionManager {

    var isJumpFromSettingPage = false

    /**
     * 检查应用必要的权限
     * @param mOnDenied 如果非空则又业务逻辑结棍拒绝权限的逻辑，不会弹出跳转至应用设置的弹窗
     */
    fun checkStoragePermission(
        _mActivity: Activity?,
        mOnGranted: (permissions: List<String>?) -> Unit,
        mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: List<String>, permissionsDenied: List<String>) -> Unit)? = null
    ) {
        checkPermission(_mActivity, PermissionConstants.getPermissions(PermissionConstants.STORAGE), mOnGranted, mOnDenied)
    }

    /**
     * 检查获取图片的权限
     * @param mOnDenied 如果非空则又业务逻辑结棍拒绝权限的逻辑，不会弹出跳转至应用设置的弹窗
     */
    fun checkPhotoPermission(
        _mActivity: Activity?,
        mOnGranted: (permissions: List<String>?) -> Unit,
        mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: List<String>, permissionsDenied: List<String>) -> Unit)? = null
    ) {
        checkPermission(_mActivity, arrayOf(PermissionConstants.PHONE), mOnGranted, mOnDenied)
    }

    /**
     * 检查相机的权限
     * @param mOnDenied 如果非空则又业务逻辑结棍拒绝权限的逻辑，不会弹出跳转至应用设置的弹窗
     */
    fun checkCameraPermission(
        _mActivity: Activity?,
        mOnGranted: (permissions: List<String>?) -> Unit,
        mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: List<String>, permissionsDenied: List<String>) -> Unit)? = null
    ) {
        checkPermission(_mActivity, arrayOf(PermissionConstants.CAMERA), mOnGranted, mOnDenied)
    }

    /**
     * 检查语音聊天的权限
     * @param mOnDenied 如果非空则又业务逻辑结棍拒绝权限的逻辑，不会弹出跳转至应用设置的弹窗
     */
    fun checkVoiceLinkPermission(
        _mActivity: Activity?,
        mOnGranted: (permissions: List<String>?) -> Unit,
        mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: List<String>, permissionsDenied: List<String>) -> Unit)? = null
    ) {
        val group = arrayOf(PermissionConstants.MICROPHONE)
        checkPermission(_mActivity, group, mOnGranted, mOnDenied)
    }

    /**
     * 检查视频聊天的权限
     * @param mOnDenied 如果非空则又业务逻辑结棍拒绝权限的逻辑，不会弹出跳转至应用设置的弹窗
     */
    @SuppressLint("WrongConstant")
    fun checkVideoLinkPermission(
        _mActivity: Activity?,
        mOnGranted: (permissions: List<String>?) -> Unit,
        mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: List<String>, permissionsDenied: List<String>) -> Unit)? = null
    ) {
        val group: Array<String> = arrayOf(PermissionConstants.CAMERA, PermissionConstants.MICROPHONE)
        checkPermission(_mActivity, group, mOnGranted, mOnDenied)
    }

    private fun checkPermission(
        _mActivity: Activity?,
        @PermissionConstants.PermissionGroup permissionGroup: Array<String>,
        mOnGranted: (permissions: List<String>?) -> Unit,
        mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: List<String>, permissionsDenied: List<String>) -> Unit)?
    ) {
        if (_mActivity == null) return
        PermissionUtils.permissionGroup(*permissionGroup).callback(PermissionSelfCallback(_mActivity, mOnGranted, mOnDenied)).request()
    }

    /** 显示默认的权限被永远拒绝的提示 */
    private fun showDeniedForeverTip(context: Context) {
        AppConfirmPopup.Builder(context)
            .setContent(StringUtils.getString(R.string.hc_default_permission_denied_forever_warning))
            .setSubmitText(StringUtils.getString(R.string.hc_word_confirm))
            .setCancelText(StringUtils.getString(R.string.hc_word_cancel))
            .setOnClickSubmitListener {
                PermissionUtils.launchAppDetailsSettings()
            }
            .build()
            .show()
    }

    private class PermissionSelfCallback(
        private val activity: Activity,
        private val mOnGranted: (permissions: List<String>?) -> Unit,
        private val mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: List<String>, permissionsDenied: List<String>) -> Unit)? = null
    ) : PermissionUtils.FullCallback {
        override fun onGranted(granted: MutableList<String>) {
            mOnGranted.invoke(granted)
        }

        override fun onDenied(deniedForever: MutableList<String>, denied: MutableList<String>) {
            val isDeniedForever = deniedForever.isNotEmpty()
            if (mOnDenied == null) {
                if (isDeniedForever) showDeniedForeverTip(activity)
            } else mOnDenied.invoke(isDeniedForever, deniedForever, denied)
        }
    }
}