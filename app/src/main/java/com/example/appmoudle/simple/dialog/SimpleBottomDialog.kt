package com.example.appmoudle.simple.dialog

import android.content.Context
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseBottomPopupView
import com.example.appmoudle.databinding.LayoutSimpleBinding
import com.lxj.xpopup.XPopup
import com.weikaiyun.fragmentation.SupportFragment


/**
 * CreateBy:Joker
 * description：底部弹窗
 */
class SimpleBottomDialog(context: Context) : BaseBottomPopupView<LayoutSimpleBinding>(context) {

    override fun getViewBinding() = LayoutSimpleBinding.bind(popupImplView)
    override fun getImplLayoutId() = R.layout.layout_simple

    companion object {
        fun show(fragment: SupportFragment, onConfirm: (() -> Unit)? = null): SimpleBottomDialog? {
            fragment.context?.let {
                val dialog = SimpleBottomDialog(it).apply {
                    this.fragment = fragment
                    this.onConfirm = onConfirm
                }

                XPopup.Builder(it).asCustom(dialog).show()
                return dialog
            }
            return null
        }
    }

    private var fragment: SupportFragment? = null             //内容文案
    private var onConfirm: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        dialogBinding?.apply {

        }
    }
}