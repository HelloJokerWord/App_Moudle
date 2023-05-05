package com.example.appmoudle.base

import android.os.Bundle
import com.example.appmoudle.dialog.CommonLoadingDialog
import com.lxj.xpopup.core.BasePopupView
import com.orhanobut.logger.Logger
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
        Logger.d( "$TAG onResume")
    }

    override fun onVisible() {
        super.onVisible()
        Logger.d( "$TAG onVisible")
    }

    override fun onInvisible() {
        super.onInvisible()
        Logger.d( "$TAG onInvisible")
    }

    override fun onPause() {
        super.onPause()
        Logger.d( "$TAG onPause")
    }

    override fun onStop() {
        super.onStop()
        Logger.d( "$TAG onStop")
    }

    override fun onDestroy() {
        dismissLoadingDialog()
        super.onDestroy()
        Logger.d( "$TAG onDestroy")
    }

    override fun onBackPressedSupport(): Boolean {
        Logger.d( "$TAG onBackPressedSupport")
        return super.onBackPressedSupport()
    }

    override fun onNewBundle(args: Bundle?) {
        super.onNewBundle(args)
        Logger.d( "$TAG onNewBundle args=${args}")
    }

    override fun putNewBundle(newBundle: Bundle?) {
        super.putNewBundle(newBundle)
        Logger.d( "$TAG putNewBundle newBundle=${newBundle}")
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