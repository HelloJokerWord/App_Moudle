package com.libgoogle.login

import android.content.Context
import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.MetaDataUtils
import com.libgoogle.login.entity.LoginResultEntity
import com.third.libcommon.BuildConfig
import com.third.libcommon.extension.safeLet
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.twitter.sdk.android.core.models.User

/**
 * CreateBy:Joker
 * CreateTime:2023/6/28 16:21
 * description：
 */
object TwitterLoginManager {

    private var twitterLoginButton: TwitterLoginButton? = null
    private var resultCallback: ((entity: LoginResultEntity) -> Unit)? = null

    private val twitterCallBack by lazy {
        object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                val token = result?.data?.authToken?.token
                val secret = result?.data?.authToken?.secret

                if (token.isNullOrEmpty()) {
                    resultCallback?.invoke(
                        LoginResultEntity(
                            LoginResultEntity.CODE_ERROR,
                            "Twitter Login fail, token is null",
                            LoginResultEntity.LOGIN_TYPE_TWITTER
                        )
                    )
                    return
                }
                if (secret.isNullOrEmpty()) {
                    resultCallback?.invoke(
                        LoginResultEntity(
                            LoginResultEntity.CODE_ERROR,
                            "Twitter Login fail, secret is null",
                            LoginResultEntity.LOGIN_TYPE_TWITTER
                        )
                    )
                    return
                }

                TwitterCore.getInstance()
                    .apiClient
                    .accountService
                    .verifyCredentials(true, false, true)
                    .enqueue(object : Callback<User>() {
                        override fun success(result: Result<User>?) {
                            safeLet(resultCallback, result?.data) { callback, data ->
                                if (data.id != 0L) {
                                    callback.invoke(
                                        LoginResultEntity(
                                            LoginResultEntity.CODE_SUCCESS,
                                            "twitter login success",
                                            LoginResultEntity.LOGIN_TYPE_TWITTER,
                                            third_id = data.idStr,
                                            third_token = token,
                                            thirdSecret = secret,
                                            email = data.email,
                                            loginname = data.name,
                                            avatar = data.profileImageUrlHttps
                                        )
                                    )
                                } else {
                                    resultCallback?.invoke(
                                        LoginResultEntity(
                                            LoginResultEntity.CODE_ERROR,
                                            "can not get id",
                                            LoginResultEntity.LOGIN_TYPE_TWITTER
                                        )
                                    )
                                }
                            }
                        }

                        override fun failure(exception: TwitterException?) {
                            exception?.printStackTrace()
                            resultCallback?.invoke(
                                LoginResultEntity(
                                    LoginResultEntity.CODE_ERROR,
                                    exception?.message,
                                    LoginResultEntity.LOGIN_TYPE_TWITTER
                                )
                            )
                        }
                    })
            }

            override fun failure(exception: TwitterException?) {
                exception?.printStackTrace()
                resultCallback?.invoke(
                    LoginResultEntity(
                        LoginResultEntity.CODE_ERROR,
                        exception?.message,
                        LoginResultEntity.LOGIN_TYPE_TWITTER
                    )
                )
            }
        }
    }

    /**
     * Twitter初始化
     */
    fun initTwitterLogin(context: Context) {
        val twitterConsumerKey = MetaDataUtils.getMetaDataInApp("twitter_login_key")
        val twitterConsumerSecret = MetaDataUtils.getMetaDataInApp("twitter_login_secret")

        val config = TwitterConfig.Builder(context)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(twitterConsumerKey, twitterConsumerSecret))
            .debug(BuildConfig.DEBUG)
            .build()
        Twitter.initialize(config)
    }

    /**
     * Twitter登录
     */
    fun startLogin(context: Context, resultCallback: (entity: LoginResultEntity) -> Unit) {
        if (twitterLoginButton == null) twitterLoginButton = TwitterLoginButton(context)

        this.resultCallback = resultCallback
        twitterLoginButton?.let {
            it.callback = twitterCallBack
            it.callOnClick()
        }
    }

    /**
     * Twitter回调
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        twitterLoginButton?.onActivityResult(requestCode, resultCode, data)
    }

    fun onRelease() {
        resultCallback = null
    }

}
