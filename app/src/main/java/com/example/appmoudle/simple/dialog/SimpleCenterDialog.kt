package com.example.appmoudle.simple.dialog

import android.content.Context
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseCenterPopupView
import com.example.appmoudle.databinding.LayoutSimpleBinding
import com.lxj.xpopup.XPopup
import com.weikaiyun.fragmentation.SupportFragment


/**
 * CreateBy:Joker
 * CreateTime:2022/11/16 10:28
 * description：
 */
class SimpleCenterDialog(context: Context) : BaseCenterPopupView<LayoutSimpleBinding>(context) {

    override fun getViewBinding() = LayoutSimpleBinding.bind(popupImplView)
    override fun getImplLayoutId() = R.layout.layout_simple

    companion object {
        fun show(fragment: SupportFragment, content: String?, onConfirm: (() -> Unit)? = null): SimpleCenterDialog? {
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