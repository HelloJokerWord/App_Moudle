package com.example.appmoudle.base

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.orhanobut.logger.Logger
import com.weikaiyun.fragmentation.SupportActivity
import com.weikaiyun.fragmentation.SupportHelper

abstract class BaseSupportActivity<VB : ViewBinding> : SupportActivity() {

    val TAG = javaClass.simpleName

    protected var mViewBinding: VB? = null

    abstract fun getViewBinding(): VB?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("onCreate $TAG")
        mViewBinding = getViewBinding()
        mViewBinding?.apply { setContentView(root) }

        ScreenUtils.setNonFullScreen(this)
    }

    override fun onRestart() {
        super.onRestart()
        Logger.d("$TAG onRestart")
    }

    override fun onStart() {
        super.onStart()
        Logger.d("$TAG onStart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Logger.d("$TAG onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        Logger.d("$TAG onResume")
    }

    override fun onPause() {
        super.onPause()
        Logger.d("$TAG onPause")
    }

    override fun onStop() {
        super.onStop()
        Logger.d("$TAG onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding = null
        Logger.d("$TAG onDestroy")
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d("requestCode=$requestCode resultCode=$resultCode data=$data")
        //Google Play 更新
        //GooglePlayManager.onActivityResult(requestCode, resultCode, data)

        val listFragment = SupportHelper.getActiveFragments(supportFragmentManager)
        listFragment.forEach {
            if (it is BaseFragment) {
                Logger.w("传递 name=${it.javaClass.simpleName}")
                it.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}