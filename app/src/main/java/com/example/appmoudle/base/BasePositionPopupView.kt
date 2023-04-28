package com.example.appmoudle.base

import android.content.Context
import android.util.Log
import androidx.viewbinding.ViewBinding
import com.lxj.xpopup.core.PositionPopupView


/**
 * Description: 任意位置弹窗
 */
abstract class BasePositionPopupView<T : ViewBinding>(context: Context) : PositionPopupView(context) {

    val TAG = javaClass.simpleName
    protected var dialogBinding: T? = null

    abstract fun getViewBinding(): T?

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