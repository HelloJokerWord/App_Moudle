package com.third.libcommon.countdown;

/**
 * 创建人: Joker
 * 创建日期: 2020/5/30. 11:35
 * 说明:
 */
public interface OnCountDownTimerListener {

    void onTick(long millisUntilFinished);

    void onFinish();
}
