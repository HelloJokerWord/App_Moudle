package com.third.libcommon.countdown

import com.blankj.utilcode.constant.TimeConstants

/**
 * CreateBy:Joker
 * CreateTime:2023/5/5 10:25
 * description：倒计时使用模版
 */
class SimpleTimerUsed {

    fun test() {
        CountDownTimerSupport(5 * TimeConstants.SEC.toLong(), TimeConstants.SEC.toLong()).apply {
            //倒计时监听
            setOnCountDownTimerListener(object : OnCountDownTimerListener {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                }
            })

            //启动倒计时
            start()
        }
    }
}