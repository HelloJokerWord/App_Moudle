package com.example.simple.dialog

import android.content.Context
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseCenterPopupView
import com.example.appmoudle.base.BaseFragment
import com.example.appmoudle.databinding.AppSimpleLayoutEmptyBinding
import com.lxj.xpopup.XPopup


/**
 * CreateBy:Joker
 * description：中间弹窗
 */
class SimpleCenterDialog(context: Context) : BaseCenterPopupView<AppSimpleLayoutEmptyBinding>(context) {

    override fun getViewBinding() = AppSimpleLayoutEmptyBinding.bind(popupImplView)
    override fun getImplLayoutId() = R.layout.app_simple_layout_empty

    companion object {
        fun show(fragment: BaseFragment, content: String?, onConfirm: (() -> Unit)? = null): SimpleCenterDialog? {
            fragment.context?.let {
                val dialog = SimpleCenterDialog(it).apply {
                    this.content = content
                    this.onConfirm = onConfirm
                }

                XPopup.Builder(it).asCustom(dialog).show()
                return dialog
            }
            return null
        }
    }

    private var content: String? = null             //内容文案
    private var onConfirm: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        dialogBinding?.apply {

        }
    }
}