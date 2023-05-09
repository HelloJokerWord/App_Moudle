package com.example.appmoudle.base

import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.example.appmoudle.dialog.CommonLoadingDialog
import com.lxj.xpopup.core.BasePopupView
import com.weikaiyun.fragmentation.SupportFragment

/**
 * Created on 2022/12/23.
 * @author Joker
 * Des:
 */

abstract class BaseFragment : SupportFragment() {

    protected val TAG = javaClass.simpleName
    private var loadingDialog: BasePopupView? = null

    override fun onResume() {
        super.onResume()
        LogUtils.d( "$TAG onResume")
    }

    override fun onVisible() {
        super.onVisible()
        LogUtils.d( "$TAG onVisible")
    }

    override fun onInvisible() {
        super.onInvisible()
        LogUtils.d( "$TAG onInvisible")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.d( "$TAG onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.d( "$TAG onStop")
    }

    override fun onDestroy() {
        dismissLoadingDialog()
        super.onDestroy()
        LogUtils.d( "$TAG onDestroy")
    }

    override fun onBackPressedSupport(): Boolean {
        LogUtils.d( "$TAG onBackPressedSupport")
        return super.onBackPressedSupport()
    }

    override fun onNewBundle(args: Bundle?) {
        super.onNewBundle(args)
        LogUtils.d( "$TAG onNewBundle args=${args}")
    }

    override fun putNewBundle(newBundle: Bundle?) {
        super.putNewBundle(newBundle)
        LogUtils.d( "$TAG putNewBundle newBundle=${newBundle}")
    }

    /**
     * 页面内异步操作都建议加上这个判断
     */
    fun isFragmentDestroyed() = !(isAdded && !isDetached && context != null)

    /** 显示加载框 */
    fun showLoadingDialog() {
        loadingDialog = CommonLoadingDialog.show(context)
    }

    /** 关闭加载框 */
    fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}