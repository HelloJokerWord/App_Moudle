package com.example.appmoudle.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.appmoudle.base.BaseSupportFragment
import com.example.appmoudle.databinding.FLoginBinding


/**
 * Created on 2022/9/5.
 * @author Joker
 * Des:
 */

class LoginF : BaseSupportFragment<FLoginBinding>() {

    companion object {
        fun newInstance() = LoginF()
    }

    override fun isDarkStatusBarFont() = false
    override fun isNeedPaddingTop() = false
    override fun getViewBinding(inflater: LayoutInflater) = FLoginBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding?.apply {
        }
    }


}