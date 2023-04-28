package com.example.appmoudle.config

import com.blankj.utilcode.util.PathUtils

/**
 * Created on 2022/9/7.
 * @author Joker
 * Des: 全应用本地磁盘使用路径
 */

object HCPath {

    /**
     * 根路径
     */
    private val ROOT_PATH = PathUtils.getInternalAppCachePath()

    private const val DATA_BASE_VERSION = 6  //每次数据库修改字段等，需要升级，以免覆盖安装奔溃

    /**
     * 创建主数据库文件路径
     */
    fun createMainDBPath() = "${PathUtils.getInternalAppDbsPath()}/objectbox/db_version_$DATA_BASE_VERSION"

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
    val LOG_PATH = "$ROOT_PATH/log/log"

    /**
     * 图片压缩路径
     */
    val IMAGE_CACHE = "$ROOT_PATH/image/"

    /**
     * 图片下载地址
     */
    val IMAGE_DOWNLOAD = "$ROOT_PATH/image_download/"

    /**
     * 本地生成录音存临时放位置
     */
    val RECORD_TEMP_FILE = "$ROOT_PATH/record/temp/"

    /**
     * 录音缓存位置
     */
    val RECORD_FILE = "$ROOT_PATH/record/"

    /**
     * 拍照输出
     */
    val CAMERA_OUT_PUT = "$ROOT_PATH/camerax/"

    /**
     * 裁剪输出
     */
    val CROP_OUT_PUT = "$ROOT_PATH/crop/"

    /**
     * 头像框svga文件地址
     */
    val WEAR_AVATAR_SVGA = "$ROOT_PATH/wear_avatar/"

}