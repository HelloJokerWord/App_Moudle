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
import com.example.appmoudle.login.bean.LoginBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import rxhttp.async
import rxhttp.toAwait
import rxhttp.tryAwait
import rxhttp.wrapper.param.RxHttp

/**
 *
 */
class SimpleF : BaseSupportFragment<LayoutSimpleBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) = LayoutSimpleBinding.inflate(inflater)

    companion object {
        fun startF(fragment: BaseFragment): SimpleF {
            val f = SimpleF()
            val bundle = Bundle()

            f.arguments = bundle
            fragment.start(f)
            return f
        }
    }

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
}