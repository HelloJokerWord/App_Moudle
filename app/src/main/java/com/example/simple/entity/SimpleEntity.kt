package com.example.simple.entity

import androidx.annotation.Keep
import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created on 2023/2/21.
 * @author Joker
 * Des:
 */

@Keep
data class SimpleEntity(
    val type: Int,
) : MultiItemEntity {

    override val itemType = type

    companion object {
        const val TYPE_SIMPLE_ONE = 1
        const val TYPE_SIMPLE_TWO = 2
    }
}
