package com.example.appmoudle.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.blankj.utilcode.util.ReflectUtils
import com.example.appmoudle.R
import com.example.appmoudle.databinding.LayoutAppStateViewLoadingBinding
import com.github.nukc.stateview.StateView

@SuppressLint("NonConstantResourceId")
class StateViewManager private constructor(private val mStateView: StateView) {

    companion object {
        const val DEFAULT_EMPTY_RESOURCE_ID = R.layout.layout_app_state_view_empty
        const val DEFAULT_RETRY_RESOURCE_ID = R.layout.layout_app_state_view_retry
        const val DEFAULT_LOADING_RESOURCE_ID = R.layout.layout_app_state_view_loading

        fun inject(view: View): StateViewManager {
            return StateViewManager(StateView.inject(view))
        }

        fun inject(activity: Activity): StateViewManager {
            return StateViewManager(StateView.inject(activity))
        }

        fun inject(viewGroup: ViewGroup): StateViewManager {
            return StateViewManager(StateView.inject(viewGroup))
        }
    }

    private var mCurrentStatus = Status.CONTENT

    private val mReflectObj = ReflectUtils.reflect(mStateView)

    private val mOnViewStatusChangeListeners = ArrayList<(oldViewStatus: Status, newViewStatus: Status) -> Unit>(2)

    init {
        if (getLoadingViewResId() == com.github.nukc.stateview.R.layout.base_loading) setLoadingResId(DEFAULT_LOADING_RESOURCE_ID)
        if (getRetryViewResId() == com.github.nukc.stateview.R.layout.base_retry) setRetryResId(DEFAULT_RETRY_RESOURCE_ID)
        if (getEmptyViewResId() == com.github.nukc.stateview.R.layout.base_empty) setEmptyResId(DEFAULT_EMPTY_RESOURCE_ID)

        addOnViewStatusChangeListener { oldViewStatus, newViewStatus ->
            when {
                newViewStatus == Status.LOADING -> {
                    if (getLoadingViewResId() == DEFAULT_LOADING_RESOURCE_ID) onShowLoadingSVGA()
                }

                oldViewStatus == Status.LOADING -> {
                    if (getLoadingViewResId() == DEFAULT_LOADING_RESOURCE_ID) onCancelLoadingSVGA()
                }
            }
        }
    }

    fun setEmptyResId(@LayoutRes emptyResource: Int): StateViewManager {
        mStateView.emptyResource = emptyResource
        return this
    }

    fun setRetryResId(@LayoutRes retryResource: Int): StateViewManager {
        mStateView.retryResource = retryResource
        return this
    }

    fun setLoadingResId(@LayoutRes loadingResource: Int): StateViewManager {
        mStateView.loadingResource = loadingResource
        return this
    }

    /** 获取加载中页面的Id  */
    private fun getLoadingViewResId(): Int {
        return mReflectObj.field("loadingResource").get()
    }

    /** 获取错误页面的Id  */
    private fun getRetryViewResId(): Int {
        return mReflectObj.field("retryResource").get()
    }

    /** 获取空布局页面的Id  */
    private fun getEmptyViewResId(): Int {
        return mReflectObj.field("emptyResource").get()
    }

    fun showEmpty() {
        dispatchViewStatusChangeListener(Status.EMPTY)
        mCurrentStatus = Status.EMPTY
        mStateView.showEmpty()
    }

    fun showLoading() {
        dispatchViewStatusChangeListener(Status.LOADING)
        mCurrentStatus = Status.LOADING
        mStateView.showLoading()
    }

    fun showRetry() {
        dispatchViewStatusChangeListener(Status.RETRY)
        mCurrentStatus = Status.RETRY
        mStateView.showRetry()
    }

    fun showContent() {
        dispatchViewStatusChangeListener(Status.CONTENT)
        mCurrentStatus = Status.CONTENT
        mStateView.showContent()
    }

    fun isEmptyStatus(): Boolean {
        return mCurrentStatus == Status.EMPTY
    }

    fun isLoadingStatus(): Boolean {
        return mCurrentStatus == Status.LOADING
    }

    fun isRetryStatus(): Boolean {
        return mCurrentStatus == Status.RETRY
    }

    fun isContentStatus(): Boolean {
        return mCurrentStatus == Status.CONTENT
    }

    fun setOnInflateListener(listener: (layoutResource: Int, view: View) -> Unit): StateViewManager {
        mStateView.onInflateListener = object : StateView.OnInflateListener {
            override fun onInflate(layoutResource: Int, view: View) {
                listener.invoke(layoutResource, view)
            }
        }
        return this
    }

    /** 不需要调用showLoading()方法, StateView自会调用 */
    fun setOnRetryClickListener(listener: () -> Unit): StateViewManager {
        mStateView.onRetryClickListener = object : StateView.OnRetryClickListener {
            override fun onRetryClick() {
                listener.invoke()
            }
        }
        return this
    }

    fun addOnViewStatusChangeListener(listener: (oldViewStatus: Status, newViewStatus: Status) -> Unit) {
        mOnViewStatusChangeListeners.add(listener)
    }

    private fun dispatchViewStatusChangeListener(newStatus: Status) {
        val oldStatus = mCurrentStatus
        if (oldStatus == newStatus) return
        mOnViewStatusChangeListeners.forEach { it.invoke(oldStatus, newStatus) }
    }

    /** 播放加载中的SVGA动画 */
    private fun onShowLoadingSVGA() {
        mStateView.post {
            val mLoadingView = mReflectObj.field("views").get<SparseArray<View>>().get(DEFAULT_LOADING_RESOURCE_ID)
            val svgaView = LayoutAppStateViewLoadingBinding.bind(mLoadingView).svgaImageView
            SvgaManager.startPageLoading(svgaView)
        }
    }

    /** 取消加载中的SVGA动画 */
    private fun onCancelLoadingSVGA() {
        val mLoadingView = mReflectObj.field("views").get<SparseArray<View>>().get(DEFAULT_LOADING_RESOURCE_ID)
        val svgaView = LayoutAppStateViewLoadingBinding.bind(mLoadingView).svgaImageView
        SvgaManager.stopPlay(svgaView)
    }

    enum class Status {
        LOADING, CONTENT, RETRY, EMPTY
    }


}