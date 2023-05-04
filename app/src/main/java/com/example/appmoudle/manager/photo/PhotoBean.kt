package com.example.appmoudle.manager.photo

import androidx.annotation.Keep
import java.io.File

/**
 * Created on 2023/1/13.
 * @author Joker
 * Des:
 */

@Keep
data class PhotoBean(
    val path: String?,
    val file: File,
    val width: Int,
    val height: Int,
)
