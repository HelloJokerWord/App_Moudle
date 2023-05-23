package com.example.appmoudle.dialog

import android.content.Context
import cc.taylorzhang.singleclick.onSingleClick
import com.blankj.utilcode.util.StringUtils
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseCenterPopupView
import com.example.appmoudle.databinding.DialogCommonConfirmBinding
import com.lxj.xpopup.XPopup

class CommonConfirmDialog(context: Context) : BaseCenterPopupView<DialogCommonConfirmBinding>(context) {

    override fun getViewBinding() = DialogCommonConfirmBinding.bind(popupImplView)
    override fun getImplLayoutId() = R.layout.dialog_common_confirm

    companion object {
        fun show(
            context: Context?,
            content: String?,
            cancelText: String? = StringUtils.getString(R.string.hc_word_cancel),
            confirmText: String? = StringUtils.getString(R.string.hc_word_confirm),
            onCancel: (() -> Unit)? = null,
            onConfirm: (() -> Unit)? = null
        ): CommonConfirmDialog? {
            context?.let {
                val dialog = CommonConfirmDialog(it).apply {
                    this.content = content
                    this.cancelText = cancelText
                    this.confirmText = confirmText
                    this.onCancel = onCancel
                    this.onConfirm = onConfirm
                }

                XPopup.Builder(it).asCustom(dialog).show()
                return dialog
            }
            return null
        }
    }

    private var content: String? = null             //内容文案
    private var cancelText: String? = null          //取消文案
    private var confirmText: String? = null         //确定文案
    private var onCancel: (() -> Unit)? = null
    private var onConfirm: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        dialogBinding?.apply {
            tvContent.text = content
            tvCancel.text = cancelText
            tvSubmit.text = confirmText

            tvCancel.onSingleClick {
                onCancel?.invoke()
                dismiss()
            }
            tvSubmit.onSingleClick {
                onConfirm?.invoke()
                dismiss()
            }
        }
    }
}