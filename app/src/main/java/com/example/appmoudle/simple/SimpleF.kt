package com.example.appmoudle.simple

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.appmoudle.base.BaseFragment
import com.example.appmoudle.base.BaseSupportFragment
import com.example.appmoudle.databinding.LayoutSimpleBinding

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