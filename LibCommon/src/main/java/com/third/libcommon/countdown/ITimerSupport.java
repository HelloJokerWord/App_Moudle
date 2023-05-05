package com.third.libcommon.countdown;

/**
 * 创建人: Joker
 * 创建日期: 2020/5/30. 11:34
 * 说明:
 */
public interface ITimerSupport {
    void start();

    void pause();

    void resume();

    void stop();

    void cancel();

    void reset();
}
