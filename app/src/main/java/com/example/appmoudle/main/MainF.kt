package com.example.appmoudle.main

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.FileUtils
import com.example.appmoudle.agentweb.CommonWebF
import com.example.appmoudle.base.BaseSupportFragment
import com.example.appmoudle.config.GlobalPath
import com.example.appmoudle.databinding.FMainBinding
import com.example.appmoudle.dialog.CommonBottomWebDialog
import com.example.appmoudle.manager.photo.PhotosManager
import com.orhanobut.logger.Logger
import com.third.libcommon.log.LoggerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created on 2022/9/5.
 * @author Joker
 * Des:
 */

class MainF : BaseSupportFragment<FMainBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) = FMainBinding.inflate(inflater)
    override fun isNeedPaddingTop() = false

    companion object {
        const val PAGE_HOME = 0    //主页
        const val PAGE_PARTY = 1   //派对页
        const val PAGE_CHAT = 2    //聊天列表
        const val PAGE_ME = 3      //我的页面

        fun newInstance() = MainF()
    }

    private var currentPage = PAGE_HOME                         //当前页面
    private var animDrawable: AnimationDrawable? = null
    private var currentMsgCount = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding?.apply {
//            val homeFragmentInStack = findChildFragment(HomeF::class.java)
//            if (homeFragmentInStack != null) {
//                homeF = homeFragmentInStack
//                partyF = findChildFragment(PartyF::class.java)
//                messageF = findChildFragment(MessageF::class.java)
//                mineF = findChildFragment(MineF::class.java)
//            } else {
//                homeF = HomeF.newInstance()
//                partyF = PartyF.newInstance()
//                messageF = MessageF.newInstance()
//                mineF = MineF.newInstance()
//                loadMultipleRootFragment(R.id.flMain, PAGE_HOME, homeF, partyF, messageF, mineF)
//            }

            clHome.setOnClickListener { showHome() }
            clParty.setOnClickListener { showParty() }
            clChat.setOnClickListener { showMessage() }
            clMe.setOnClickListener { showMe() }
        }

        //默认显示第一页
        showHome()

        PhotosManager.openAlbum(activity){

        }
    }

    /**
     * 显示home页面
     */
    fun showHome() {
        currentPage = PAGE_HOME
        //showHideFragment(homeF)
        resetTabStatus()
        mViewBinding?.apply {
//            ivTabChat.setImageResource(R.drawable.hc_anim_tab_message)
//            tvTabChat.setTextColor(ColorUtils.getColor(R.color.color_FF5555))
//            startAnimation(ivTabChat)
        }
    }

    /**
     * 显示party页面
     */
    fun showParty() {
        currentPage = PAGE_PARTY
        //showHideFragment(partyF)
        resetTabStatus()
        mViewBinding?.apply {
        }
    }

    /**
     * 显示聊天
     */
    fun showMessage() {
        currentPage = PAGE_CHAT
        //showHideFragment(messageF)
        resetTabStatus()
        mViewBinding?.apply {

        }
    }


    /**
     * 显示Me页面
     */
    fun showMe() {
        currentPage = PAGE_ME
        //showHideFragment(mineF)
        resetTabStatus()
        mViewBinding?.apply {
        }
    }

    /**
     * 重置所有tabUI样式
     */
    private fun resetTabStatus() {
        mViewBinding?.apply {
            animDrawable?.stop()

        }
    }

    /**
     * 播放贞动画
     */
    private fun startAnimation(ivTab: ImageView) {
        animDrawable = ivTab.drawable as AnimationDrawable
        animDrawable?.stop()
        animDrawable?.isOneShot = true
        animDrawable?.start()
    }
}