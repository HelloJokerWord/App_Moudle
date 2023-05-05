package com.example.appmoudle.manager.resdownload

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Created on 2023/2/17.
 * @author Joker
 * Des:
 */

@Keep
data class ResListEntity(
    val list: MutableList<ResItemEntity>?,
    val version: Int
)

@Parcelize
@Keep
data class ResItemEntity(
    val res_url: String? = "",
    val res_name: String? = "",
    val res_type: Int = 0,
    val res_id: Int = 0,
    val resource_sku_list: MutableList<ResSkuEntity>? = null,
    var in_use: Int = 0,                             //是否使用选中
    val active_duration: Int = 0,                    //剩余时间
) : Parcelable {
    companion object {
        const val TYPE_IMG = 1
        const val TYPE_SVGA = 2
    }

    fun isUsed() = in_use == 1
}

@Parcelize
@Keep
data class ResSkuEntity(
    val active_day: Int,
    val gold_price: Int,
    var isSelect: Boolean = false,
) : Parcelable


