package com.example.appmoudle.base

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.weikaiyun.fragmentation.SupportFragment

/**
 * Created on 2022/6/30.
 * @author Joker
 * Des: 主页vp fragment适配器
 */

class BaseFragmentStateAdapter(activity: FragmentActivity, private val list: MutableList<SupportFragment>) : FragmentStateAdapter(activity) {

    override fun getItemCount() = list.size

    override fun createFragment(position: Int) = list[position]
}