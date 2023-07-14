package com.example.simple

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.simple.entity.SimpleReqEntity
import com.libhttp.HttpManager
import com.libhttp.RequestCallBack
import com.libhttp.URLApi

/**
 * Created on 2022/9/13.
 * @author Joker
 * Des:数据中心
 */

class SimpleViewModel(application: Application) : AndroidViewModel(application) {

    val loginData = MutableLiveData<SimpleReqEntity?>()

    /**
     * 请求数据处理
     */
    fun googleLogin(owner: LifecycleOwner) {
        HttpManager.postBody(owner, URLApi.URL_EMAIL_LOGIN, mutableMapOf("1" to 2), object : RequestCallBack<SimpleReqEntity?> {
            override fun onSuccess(data: SimpleReqEntity?) {
                loginData.postValue(data)
            }

            override fun onFail(code: Int, msg: String?) {
                loginData.postValue(null)
            }
        })
    }
}