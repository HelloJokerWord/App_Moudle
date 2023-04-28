package com.example.appmoudle.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.weikaiyun.fragmentation.SupportActivity
import com.weikaiyun.fragmentation.SupportHelper

abstract class BaseSupportActivity<VB : ViewBinding> : SupportActivity() {

    val TAG = javaClass.simpleName

    protected var mViewBinding: VB? = null

    abstract fun getViewBinding(): VB?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate $TAG")
        mViewBinding = getViewBinding()
        mViewBinding?.apply { setContentView(root) }

        ScreenUtils.setNonFullScreen(this)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "$TAG onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "$TAG onStart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "$TAG onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "$TAG onResume")
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
        super.onDestroy()
        mViewBinding = null
        Log.d(TAG, "$TAG onDestroy")
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode=$requestCode resultCode=$resultCode data=$data")
        //Google Play 更新
        //GooglePlayManager.onActivityResult(requestCode, resultCode, data)

        val listFragment = SupportHelper.getActiveFragments(supportFragmentManager)
        listFragment.forEach {
            if (it is BaseFragment) {
                Log.w(TAG, "传递 name=${it.javaClass.simpleName}")
                it.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}