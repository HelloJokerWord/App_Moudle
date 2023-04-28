package com.example.appmoudle.base

import android.content.Context
import android.util.Log
import androidx.viewbinding.ViewBinding
import com.lxj.xpopup.core.CenterPopupView


/**
 * Description: 居中弹窗
 */
abstract class BaseCenterPopupView<T : ViewBinding>(context: Context) : CenterPopupView(context) {

    val TAG = javaClass.simpleName
    protected var dialogBinding: T? = null

    abstract fun getViewBinding(): T?

    override fun getMaxWidth() = 0

    override fun onCreate() {
        super.onCreate()
        dialogBinding = getViewBinding()
        Log.d(TAG, "onCreate $TAG")
    }

    override fun onShow() {
        super.onShow()
        Log.d(TAG, "onShow $TAG")
    }

    override fun onDismiss() {
        super.onDismiss()
        Log.d(TAG, "onDismiss $TAG")
    }
}