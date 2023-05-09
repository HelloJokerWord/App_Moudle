package com.example.appmoudle.base

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.weikaiyun.fragmentation.SupportActivity
import com.weikaiyun.fragmentation.SupportHelper

abstract class BaseSupportActivity<VB : ViewBinding> : SupportActivity() {

    val TAG = javaClass.simpleName

    protected var mViewBinding: VB? = null

    abstract fun getViewBinding(): VB?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d("onCreate $TAG")
        mViewBinding = getViewBinding()
        mViewBinding?.apply { setContentView(root) }

        ScreenUtils.setNonFullScreen(this)
    }

    override fun onRestart() {
        super.onRestart()
        LogUtils.d("$TAG onRestart")
    }

    override fun onStart() {
        super.onStart()
        LogUtils.d("$TAG onStart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        LogUtils.d("$TAG onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.d("$TAG onResume")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.d("$TAG onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.d("$TAG onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding = null
        LogUtils.d("$TAG onDestroy")
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.d("requestCode=$requestCode resultCode=$resultCode data=$data")
        //Google Play 更新
        //GooglePlayManager.onActivityResult(requestCode, resultCode, data)

        val listFragment = SupportHelper.getActiveFragments(supportFragmentManager)
        listFragment.forEach {
            if (it is BaseFragment) {
                LogUtils.w("传递 name=${it.javaClass.simpleName}")
                it.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}