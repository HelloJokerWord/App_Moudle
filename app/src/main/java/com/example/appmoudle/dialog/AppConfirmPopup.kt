package com.example.appmoudle.dialog

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.blankj.utilcode.util.StringUtils
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseCenterPopupView
import com.example.appmoudle.databinding.PopupAppConfirmDialogBinding
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.XPopupCallback
import java.lang.ref.WeakReference

class AppConfirmPopup(context: Context) : BaseCenterPopupView<PopupAppConfirmDialogBinding>(context) {

    private var builderParam: Params? = null

    override fun getImplLayoutId(): Int = R.layout.popup_app_confirm_dialog

    override fun getMaxWidth(): Int = ViewGroup.LayoutParams.MATCH_PARENT

    override fun getViewBinding() = PopupAppConfirmDialogBinding.bind(popupImplView)

    override fun onCreate() {
        super.onCreate()
        initView()
        initEvent()
    }

    private fun initView() {
        builderParam?.apply {
            setContentGravity(contentGravity)
            setContent(content)
            contentTextSize?.also { setContentTextSize(it) }
            contentTextColor?.also { setContentTextColor(it) }

            setCancelText(cancelText)
            setHideCancel(hideCancel)
            cancelTextColor?.also { setCancelTextColor(it) }
            cancelTextSize?.also { setCancelTextSize(it) }

            setSubmitText(submitText)
            setHideSubmit(hideSubmit)
            submitTextColor?.also { setSubmitTextColor(it) }
            submitTextSize?.also { setSubmitTextSize(it) }
        }

    }

    private fun initEvent() {
        dialogBinding?.also {
            builderParam?.apply {
                it.txtCancel.setOnClickListener {
                    onClickCancelListener?.invoke(this@AppConfirmPopup)
                    if (!interceptClose) dismiss()
                }
                it.txtSubmit.setOnClickListener {
                    onClickSubmitListener?.invoke(this@AppConfirmPopup)
                    if (!interceptClose) dismiss()
                }
            }
        }
    }

    fun setContent(content: CharSequence?) {
        dialogBinding?.txtContent?.text = content
        dialogBinding?.txtContent?.isVisible = !TextUtils.isEmpty(content)
    }

    fun setContentGravity(gravity: Int) {
        dialogBinding?.txtContent?.gravity = gravity
    }

    fun setContentTextSize(size: Float) {
        dialogBinding?.txtContent?.textSize = size
    }

    fun setContentTextColor(@ColorInt color: Int) {
        dialogBinding?.txtContent?.setTextColor(color)
    }

    fun setSubmitText(text: String?) {
        dialogBinding?.txtSubmit?.text = text
    }

    fun setCancelText(text: String?) {
        dialogBinding?.txtCancel?.text = text
    }

    fun setSubmitTextColor(color: Int) {
        dialogBinding?.txtSubmit?.setTextColor(color)
    }

    fun setCancelTextColor(color: Int) {
        dialogBinding?.txtCancel?.setTextColor(color)
    }

    fun setSubmitTextSize(size: Float) {
        dialogBinding?.txtSubmit?.textSize = size
    }

    fun setCancelTextSize(size: Float) {
        dialogBinding?.txtCancel?.textSize = size
    }

    fun setHideCancel(hide: Boolean) {
        dialogBinding?.txtCancel?.isVisible = !hide
        dialogBinding?.line2?.isVisible =
            dialogBinding?.txtCancel?.isVisible == true && dialogBinding?.txtSubmit?.isVisible == true
    }

    fun setHideSubmit(hide: Boolean) {
        dialogBinding?.txtSubmit?.isVisible = !hide
        dialogBinding?.line2?.isVisible =
            dialogBinding?.txtCancel?.isVisible == true && dialogBinding?.txtSubmit?.isVisible == true
    }

    class Params {
        var isCancelable = true
        var hideCancel = false
        var hideSubmit = false
        var interceptClose = false
        var content: CharSequence? = null
        var contentTextSize: Float? = null
        var contentTextColor: Int? = null
        var contentGravity = Gravity.CENTER
        var cancelText = StringUtils.getString(R.string.hc_word_cancel)
        var submitText = StringUtils.getString(R.string.hc_word_confirm)
        var submitTextColor: Int? = null
        var cancelTextColor: Int? = null
        var submitTextSize: Float? = null
        var cancelTextSize: Float? = null

        var onClickSubmitListener: ((dialog: AppConfirmPopup) -> Unit?)? = null
        var onClickCancelListener: ((dialog: AppConfirmPopup) -> Unit?)? = null
        var mPopupCallback: XPopupCallback? = null
    }

    class Builder(context: Context) {
        private val params = Params()

        private val mContext = WeakReference(context)

        fun setOnClickSubmitListener(listener: (dialog: AppConfirmPopup) -> Unit): Builder {
            params.onClickSubmitListener = listener
            return this
        }

        fun setOnClickCancelListener(listener: ((dialog: AppConfirmPopup) -> Unit)): Builder {
            params.onClickCancelListener = listener
            return this
        }

        fun setContent(content: CharSequence): Builder {
            params.content = content
            return this
        }

        fun setContentGravity(gravity: Int): Builder {
            params.contentGravity = gravity
            return this
        }

        fun setContentTextSize(size: Float): Builder {
            params.contentTextSize = size
            return this
        }

        fun setContentTextColor(@ColorInt color: Int): Builder {
            params.contentTextColor = color
            return this
        }

        fun setSubmitText(text: String): Builder {
            params.submitText = text
            return this
        }

        fun setCancelText(text: String): Builder {
            params.cancelText = text
            return this
        }

        fun setSubmitTextColor(color: Int): Builder {
            params.submitTextColor = color
            return this
        }

        fun setCancelTextColor(color: Int): Builder {
            params.cancelTextColor = color
            return this
        }

        fun setSubmitTextSize(size: Float) {
            params.submitTextSize = size
        }

        fun setCancelTextSize(size: Float) {
            params.cancelTextSize = size
        }

        fun setHideCancel(hide: Boolean): Builder {
            params.hideCancel = hide
            return this
        }

        fun setHideSubmit(hide: Boolean): Builder {
            params.hideSubmit = hide
            return this
        }

        /** 取消点击确认/取消后的关闭弹窗动作 */
        fun setInterceptClose(interceptClose: Boolean): Builder {
            params.interceptClose = interceptClose
            return this
        }

        /** 是否可以通过点击触摸遮罩和返回键进行关闭 */
        fun setCancelable(isCancelable: Boolean): Builder {
            params.isCancelable = isCancelable
            return this
        }

        fun setPopupCallback(callback: XPopupCallback): Builder {
            params.mPopupCallback = callback
            return this
        }

        fun build(): BasePopupView {
            val popupView = AppConfirmPopup(mContext.get()!!)
            popupView.builderParam = params
            return XPopup.Builder(mContext.get()!!)
                .dismissOnBackPressed(params.isCancelable)
                .dismissOnTouchOutside(params.isCancelable)
                .isDestroyOnDismiss(true)
                .setPopupCallback(params.mPopupCallback)
                .asCustom(popupView)
        }
    }


}