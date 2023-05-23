package com.example.appmoudle.simple

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.appmoudle.base.BaseFragment
import com.example.appmoudle.base.BaseSupportFragment
import com.example.appmoudle.databinding.LayoutSimpleBinding
import com.example.appmoudle.simple.entity.SimpleReqEntity
import com.third.libcommon.http.HttpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 带请求介绍模板
 */
class SimpleReqF : BaseSupportFragment<LayoutSimpleBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) = LayoutSimpleBinding.inflate(inflater)

    companion object {
        fun startF(fragment: BaseFragment): SimpleReqF {
            val f = SimpleReqF()
            f.arguments = Bundle().apply {

            }
            fragment.start(f)
            return f
        }
    }

    private val simpleViewModel by viewModels<SimpleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding?.apply {

        }
    }

    /**
     * 合并请求
     */
    private fun mergeReq() {
        lifecycleScope.launch(Dispatchers.Main) {
            val a = async { reqA() }
            val b = async { reqB() }
            handleData(a.await(), b.await())
        }
    }

    /**
     * 合并请求结果
     */
    private fun handleData(a: SimpleReqEntity?, b: SimpleReqEntity?) {

    }

    private suspend fun reqA(): SimpleReqEntity? = withContext(Dispatchers.IO) {
        HttpManager.getSync("", mutableMapOf(), SimpleReqEntity::class.java)
    }

    private suspend fun reqB(): SimpleReqEntity? = withContext(Dispatchers.IO) {
        HttpManager.getSync("", mutableMapOf(), SimpleReqEntity::class.java)
    }

    /**
     * 普通请求结果
     */
    private fun httpResult() {
        simpleViewModel.loginData.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            Log.i(TAG, "${it.uid}")
        }
    }

    /**
     * 普通请求发起
     */
    private fun httpReq() {
        simpleViewModel.googleLogin(viewLifecycleOwner)
    }
}