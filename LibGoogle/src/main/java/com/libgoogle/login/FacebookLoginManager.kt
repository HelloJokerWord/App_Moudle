package com.libgoogle.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.libgoogle.login.entity.LoginResultEntity


/**
 * Created by Navy on 2022/3/3
 * Desc: Facebook login
 */
object FacebookLoginManager {
    private const val TAG = "FacebookLoginManager"

    // facebook login call back
    private var callbackManager: CallbackManager? = null

    /**
     * 注册登陆
     */
    fun initLogin() {
        if (callbackManager == null) callbackManager = CallbackManager.Factory.create()
    }

    fun registerCallback(loginCallBack: (result: LoginResultEntity) -> Unit) {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.w(TAG, "fb login cancel")
                loginCallBack.invoke(
                    LoginResultEntity(
                        LoginResultEntity.CODE_CANCEL,
                        "facebook login cancel",
                        LoginResultEntity.LOGIN_TYPE_FACEBOOK
                    )
                )
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "fb login error, result: ${error.message}")
                error.printStackTrace()
                loginCallBack.invoke(
                    LoginResultEntity(
                        LoginResultEntity.CODE_ERROR,
                        "${error.message}",
                        LoginResultEntity.LOGIN_TYPE_FACEBOOK
                    )
                )
            }

            override fun onSuccess(result: LoginResult) {
                Log.i(TAG, "fb login success, result: ${result.accessToken.token}")
                getFaceBookUserInfo(result.accessToken, result.accessToken.userId, loginCallBack)
            }
        })
    }

    private fun getFaceBookUserInfo(accessToken: AccessToken?, userId: String?, loginCallBack: (result: LoginResultEntity) -> Unit) {
        val params = Bundle().apply { putString("fields", "picture,name,id,email,permissions") }
        GraphRequest(accessToken, "/me", params, HttpMethod.GET, { response ->
            val name = response.jsonObject?.optString("name")
            val avatar = response.jsonObject?.getJSONObject("picture")?.getJSONObject("data")?.optString("url")
            Log.i(TAG, "response=${response.jsonObject}")

            loginCallBack.invoke(
                LoginResultEntity(
                    LoginResultEntity.CODE_SUCCESS,
                    "fb login success",
                    LoginResultEntity.LOGIN_TYPE_FACEBOOK,
                    third_token = accessToken?.token,
                    third_id = userId,
                    loginname = name,
                    avatar = avatar,
                )
            )
        }).executeAsync()
    }

    fun unRegisterCallback() {
        LoginManager.getInstance().unregisterCallback(callbackManager)
    }

    /**
     * 登陆结果
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "requestCode=$requestCode resultCode=$resultCode data=$data")
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 退出登陆
     */
    private fun loginOut() {
        Log.w(TAG, "loginOut")
        LoginManager.getInstance().logOut()
    }

    /**
     * 开始登陆
     */
    fun doLogin(fragment: Fragment, loginCallBack: (result: LoginResultEntity) -> Unit) {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        Log.w(TAG, "开始登陆 accessToken=$accessToken  会否已登陆过=$isLoggedIn")
        if (isLoggedIn) {
            getFaceBookUserInfo(accessToken, accessToken?.userId, loginCallBack)
        } else {
            loginOut()
            fragment.activity?.let { LoginManager.getInstance().logIn(it, null) }
        }
    }
}