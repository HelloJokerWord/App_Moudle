package com.third.libcommon

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * Created on 2022/9/5.
 * @author Joker
 * Des:事件总线管理器
 */

object LiveEventManager {

    /**
     * 初始化事件总线
     */
    fun init() {
        LiveEventBus.config()
            .autoClear(true)
            .lifecycleObserverAlwaysActive(true)
            .enableLogger(BuildConfig.DEBUG)
    }

    fun <T : LiveEvent> observe(owner: LifecycleOwner, eventType: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(eventType).observe(owner, observer)
    }

    fun <T : LiveEvent> observeSticky(owner: LifecycleOwner, eventType: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(eventType).observeSticky(owner, observer)
    }

    fun <T : LiveEvent> observeForever(eventType: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(eventType).observeForever(observer)
    }

    fun <T : LiveEvent> observeStickyForever(eventType: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(eventType).observeStickyForever(observer)
    }

    fun <T : LiveEvent> removeObserve(eventType: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(eventType).removeObserver(observer)
    }

    fun post(event: LiveEvent) {
        LiveEventBus.get(event.javaClass).post(event)
    }

    fun postDelay(event: LiveEvent, time: Long) {
        LiveEventBus.get(event.javaClass).postDelay(event, time)
    }

}