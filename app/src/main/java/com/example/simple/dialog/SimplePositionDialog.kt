package com.example.simple.dialog

import android.content.Context
import com.blankj.utilcode.util.ConvertUtils
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseFragment
import com.example.appmoudle.base.BasePositionPopupView
import com.example.appmoudle.databinding.AppSimpleLayoutEmptyBinding
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.DragOrientation
import com.lxj.xpopup.enums.PopupAnimation
import com.weikaiyun.fragmentation.SupportFragment

/**
 * Description: 自定义自由定位Position弹窗
 */
class SimplePositionDialog(context: Context) : BasePositionPopupView<AppSimpleLayoutEmptyBinding>(context) {
    override fun getImplLayoutId() = R.layout.app_simple_layout_empty
    override fun getDragOrientation() = DragOrientation.DragToUp
    override fun getViewBinding() = AppSimpleLayoutEmptyBinding.bind(popupImplView)

    companion object {
        fun show(fragment: BaseFragment): SimplePositionDialog? {
            fragment.context?.let {
                val dialog = SimplePositionDialog(it).apply {
                    this.fragment = fragment
                }
                val topMargin = ImmersionBar.getStatusBarHeight(fragment) + ConvertUtils.dp2px(16f)
                XPopup.Builder(it)
                    .isCenterHorizontal(true)
                    .offsetY(topMargin)
                    .hasShadowBg(false)
                    .popupAnimation(PopupAnimation.TranslateFromTop)
                    .asCustom(dialog)
                    .show()
                return dialog
            }
            return null
        }
    }

    private var fragment: SupportFragment? = null

    override fun onCreate() {
        super.onCreate()
        dialogBinding?.apply {

        }
    }
}