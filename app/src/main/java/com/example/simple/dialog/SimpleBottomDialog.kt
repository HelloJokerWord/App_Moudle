package com.example.simple.dialog

import android.content.Context
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseBottomPopupView
import com.example.appmoudle.base.BaseFragment
import com.example.appmoudle.databinding.AppSimpleLayoutEmptyBinding
import com.lxj.xpopup.XPopup
import com.weikaiyun.fragmentation.SupportFragment


/**
 * CreateBy:Joker
 * description：底部弹窗
 */
class SimpleBottomDialog(context: Context) : BaseBottomPopupView<AppSimpleLayoutEmptyBinding>(context) {

    override fun getViewBinding() = AppSimpleLayoutEmptyBinding.bind(popupImplView)
    override fun getImplLayoutId() = R.layout.app_simple_layout_empty

    companion object {
        fun show(fragment: BaseFragment, onConfirm: (() -> Unit)? = null): SimpleBottomDialog? {
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