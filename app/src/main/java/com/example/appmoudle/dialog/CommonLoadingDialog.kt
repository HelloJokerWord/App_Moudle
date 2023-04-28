package com.example.appmoudle.dialog

import android.content.Context
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseCenterPopupView
import com.example.appmoudle.databinding.PopupAppLoadingViewBinding
import com.lxj.xpopup.XPopup

class CommonLoadingDialog(context: Context) : BaseCenterPopupView<PopupAppLoadingViewBinding>(context) {
    override fun getViewBinding() = PopupAppLoadingViewBinding.bind(popupImplView)
    override fun getImplLayoutId(): Int = R.layout.popup_app_loading_view

    companion object {
        fun show(context: Context?, isCancelTouchOutSide: Boolean = true): CommonLoadingDialog? {
            context?.let {
                val dialog = CommonLoadingDialog(it)

                XPopup.Builder(it)
                    .dismissOnTouchOutside(isCancelTouchOutSide)
                    .dismissOnBackPressed(isCancelTouchOutSide)
                    .isDestroyOnDismiss(true)
                    .asCustom(dialog)
                    .show()
                return dialog
            }
            return null
        }
    }

}