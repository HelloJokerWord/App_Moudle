package com.third.libcommon.constant

import com.blankj.utilcode.util.PathUtils
import java.io.File

/**
 * Created on 2022/9/7.
 * @author Joker
 * Des: 全应用本地磁盘使用路径
 */

object GlobalPath {

    /**
     * 根路径
     */
    private val ROOT_PATH = PathUtils.getInternalAppCachePath()

    private const val DATA_BASE_VERSION = 1  //每次数据库修改字段等，需要升级，以免覆盖安装奔溃

    /**
     * 创建主数据库文件路径
     */
    fun createMainDBPath() = "${PathUtils.getInternalAppDbsPath()}/objectbox/db_version_$DATA_BASE_VERSION"

    fun getLubanTargetPath(): String {
        val path = "$IMAGE_TEMP/Luban/image/"
        val file = File(path)
        file.mkdirs()
        return path
    }

    /**
     * http缓存路径
     */
    val HTTP_CACHE = "$ROOT_PATH/http/cache"

    /**
     * log缓存路径
     */
    val LOG_CACHE = "$ROOT_PATH/log/cache"

    /**
     * log文件上传路径
     */
    val LOG_PATH = "$ROOT_PATH/log"

    /**
     * 图片下载地址
     */
    val IMAGE_DOWNLOAD = "$ROOT_PATH/image_download"

    /**
     * 图片临时地址
     */
    private val IMAGE_TEMP = "$ROOT_PATH/image_temp"

    /**
     * 拍照输出
     */
    val CAMERA_OUT_PUT = "$ROOT_PATH/take_photo"

    /**
     * 裁剪输出
     */
    val CROP_OUT_PUT = "$ROOT_PATH/crop"

}