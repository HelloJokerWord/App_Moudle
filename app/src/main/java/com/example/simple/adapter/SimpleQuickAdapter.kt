package com.example.simple.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.appmoudle.R

/**
 * Created on 2023/1/31.
 * @author Joker
 * Des: 单布局
 */

class SimpleQuickAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.app_simple_layout_empty) {

    override fun convert(holder: BaseViewHolder, item: String) {

    }
}