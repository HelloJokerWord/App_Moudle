package com.example.simple.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.appmoudle.R
import com.example.simple.entity.SimpleEntity

/**
 * Created on 2023/1/31.
 * @author Joker
 * Des: 多布局
 */

class SimpleMultiAdapter : BaseMultiItemQuickAdapter<SimpleEntity, BaseViewHolder>() {

    init {
        addItemType(SimpleEntity.TYPE_SIMPLE_ONE, R.layout.app_simple_layout_empty)
        addItemType(SimpleEntity.TYPE_SIMPLE_TWO, R.layout.app_simple_layout_empty)
    }


    override fun convert(holder: BaseViewHolder, item: SimpleEntity) {
        when (holder.itemViewType) {
            SimpleEntity.TYPE_SIMPLE_ONE -> convertOne(holder, item)
            SimpleEntity.TYPE_SIMPLE_TWO -> convertTwo(holder, item)
        }
    }

    private fun convertTwo(holder: BaseViewHolder, item: SimpleEntity) {


    }

    private fun convertOne(holder: BaseViewHolder, item: SimpleEntity) {


    }

}