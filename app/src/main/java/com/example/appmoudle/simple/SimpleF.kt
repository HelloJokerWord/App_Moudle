package com.example.appmoudle.simple

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.appmoudle.base.BaseFragment
import com.example.appmoudle.base.BaseSupportFragment
import com.example.appmoudle.databinding.LayoutSimpleBinding

/**
 * 普通页面模板
 *
 * 常规 启动:start 关闭：pop  关闭到栈：popTo 启动并关闭到startWithPopTo
 * 具体方法可以查看 SupportFragment 提供的相关方法
 */
class SimpleF : BaseSupportFragment<LayoutSimpleBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) = LayoutSimpleBinding.inflate(inflater)

    companion object {
        fun startF(fragment: BaseFragment): SimpleF {
            val f = SimpleF()
            f.arguments = Bundle().apply {

            }
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