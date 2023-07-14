package com.third.libcommon

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils


/**
 * 作者: Joker
 * 时间: 2019/2/21. 11:34
 * 描述:
 */
object PermissionManager {

    var isJumpFromSettingPage = false

    fun checkPermission(
        _mActivity: Activity?,
        @PermissionConstants.PermissionGroup permissionGroup: Array<String>,
        mOnGranted: (permissions: MutableList<String>?) -> Unit,
        mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: MutableList<String>, permissionsDenied: MutableList<String>) -> Unit)?
    ) {
        if (_mActivity == null) return
        PermissionUtils.permissionGroup(*permissionGroup).callback(PermissionSelfCallback(_mActivity, mOnGranted, mOnDenied)).request()
    }

    /** 显示默认的权限被永远拒绝的提示 */
    private fun showDeniedForeverTip(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tips")
            .setMessage("To access your permission, go to System Settings")
            .setPositiveButton("Confirm") { _, _ ->
                PermissionUtils.launchAppDetailsSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private class PermissionSelfCallback(
        private val activity: Activity,
        private val mOnGranted: (permissions: MutableList<String>?) -> Unit,
        private val mOnDenied: ((isDeniedForever: Boolean, permissionsDeniedForever: MutableList<String>, permissionsDenied: MutableList<String>) -> Unit)? = null
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