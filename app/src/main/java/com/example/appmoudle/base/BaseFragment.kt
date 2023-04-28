package com.example.appmoudle.base

import android.os.Bundle
import android.util.Log
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
        Log.d(TAG, "$TAG onResume")
    }

    override fun onVisible() {
        super.onVisible()
        Log.d(TAG, "$TAG onVisible")
    }

    override fun onInvisible() {
        super.onInvisible()
        Log.d(TAG, "$TAG onInvisible")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "$TAG onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "$TAG onStop")
    }

    override fun onDestroy() {
        dismissLoadingDialog()
        super.onDestroy()
        Log.d(TAG, "$TAG onDestroy")
    }

    override fun onBackPressedSupport(): Boolean {
        Log.d(TAG, "$TAG onBackPressedSupport")
        return super.onBackPressedSupport()
    }

    override fun onNewBundle(args: Bundle?) {
        super.onNewBundle(args)
        Log.d(TAG, "$TAG onNewBundle args=${args}")
    }

    override fun putNewBundle(newBundle: Bundle?) {
        super.putNewBundle(newBundle)
        Log.d(TAG, "$TAG putNewBundle newBundle=${newBundle}")
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