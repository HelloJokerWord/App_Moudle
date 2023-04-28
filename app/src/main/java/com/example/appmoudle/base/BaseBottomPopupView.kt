package com.example.appmoudle.base

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView

/**
 * Description: 底部弹窗
 */
abstract class BaseBottomPopupView<T : ViewBinding>(context: Context) : BottomPopupView(context) {

    val TAG = javaClass.simpleName
    protected var dialogBinding: T? = null
    private var loadingDialog: BasePopupView? = null

    override fun getMaxHeight() = 0

    abstract fun getViewBinding(): T?

    override fun onCreate() {
        super.onCreate()
        dialogBinding = getViewBinding()
        initLifecycle()
    }

    override fun onShow() {
        super.onShow()
        Log.d(TAG, "onShow $TAG")
    }

    override fun onDismiss() {
        super.onDismiss()
        Log.d(TAG, "onDismiss $TAG")
    }

    private fun initLifecycle() {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                Log.d(TAG, "onCreate $TAG")
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                Log.d(TAG, "onStart $TAG")
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                Log.d(TAG, "onResume $TAG")
                onDialogResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                Log.d(TAG, "onPause $TAG")
                onDialogPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                Log.d(TAG, "onStop $TAG")
                onDialogStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                Log.d(TAG, "onDestroy $TAG")
            }
        })
    }

    open fun onDialogResume() {}
    open fun onDialogPause() {}
    open fun onDialogStop() {}

}