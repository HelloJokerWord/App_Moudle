package com.weikaiyun.fragmentation


/**
 * Created on 2022/9/5.
 * @author Joker
 * Des: fragment库管理： https://github.com/weikaiyun/SFragmentation
 */

object FragmentationManager {

    /**
     * 初始化
     */
    fun init() {
        // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
        Fragmentation.builder()
            .stackViewMode(Fragmentation.NONE)
            .debug(BuildConfig.DEBUG)
            .animation(R.anim.anim_fade_in, R.anim.anim_fade_out, R.anim.anim_fade_in, R.anim.anim_fade_out) //设置默认动画
            .install()
    }
}