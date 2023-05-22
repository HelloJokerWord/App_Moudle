package com.example.appmoudle.googlepay.viewmodel

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import com.example.appmoudle.googlepay.bean.BalanceBean
import com.third.libcommon.http.HttpManager
import com.third.libcommon.http.RequestCallBack


/**
 * Created on 2022/8/23.
 * @author Joker
 * Des:
 */

class PayModel(application: Application) : AndroidViewModel(application) {

    fun reqBalance(activity:FragmentActivity) {
        HttpManager.postBody(activity,"", mutableMapOf(),object : RequestCallBack<BalanceBean>{
            override fun onSuccess(data: BalanceBean) {
            }

            override fun onFail(code: Int, msg: String?) {
            }
        })
    }
}