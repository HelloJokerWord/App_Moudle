package com.example.appmoudle.simple

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.appmoudle.login.bean.LoginBean
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack
import com.third.libcommon.http.URLApi

/**
 * Created on 2022/9/13.
 * @author Joker
 * Des:数据中心
 */

class SimpleViewModel(application: Application) : AndroidViewModel(application) {

    val loginData = MutableLiveData<LoginBean?>()

    /**
     * 请求数据处理
     */
    fun googleLogin(owner: LifecycleOwner) {
        HttpManager.postBody(owner, URLApi.URL_GOOGLE_LOGIN, mutableMapOf("1" to 2), object : RequestCallBack<LoginBean?> {
            override fun onSuccess(data: LoginBean?) {
                loginData.postValue(data)
            }

            override fun onFail(code: Int, msg: String?) {
                loginData.postValue(null)
            }
        })
    }
}