package com.libgoogle.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.blankj.utilcode.util.MetaDataUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.libgoogle.login.entity.LoginResultEntity
import com.third.libcommon.constant.GlobalConstant


/**
 * Created on 2022/9/8.
 * @author Joker
 * Des: google登陆管理工具
 */
object GoogleLoginManager {

    private const val TAG = "GoogleLoginManager"

    @SuppressLint("StaticFieldLeak")
    private var mGoogleSignInClient: GoogleSignInClient? = null

    /**
     * 初始化google凭证
     */
    fun init(activity: Activity) {
        if (mGoogleSignInClient == null) {
            val key = MetaDataUtils.getMetaDataInApp("google_login_key")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(key)
                .requestServerAuthCode(key)
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        }
    }

    /**
     * 开始登陆
     */
    fun startLogin(activity: Activity, requestCode: Int) {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)
        //检查google服务是否可用
        if (code == ConnectionResult.SUCCESS) {
            //检查是否已登陆，如果已登陆，先退出，再登陆
            if (checkIsLogin(activity)) {
                signOutGoogle { signInGoogle(activity, requestCode) }
            } else {
                signInGoogle(activity, requestCode)
            }
        } else {
            showGooglePlayError(activity, code)
        }
    }

    /**
     * 登出google
     */
    private fun signOutGoogle(listener: () -> Unit) {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener {
            Log.w(TAG, "Google logout!")
            listener.invoke()
        } ?: run {
            Log.e(TAG, "登出 mGoogleSignInClient is null")
        }
    }

    /**
     * 登陆结果
     */
    fun googleLoginOnResult(intent: Intent?, resultCallBack: (data: LoginResultEntity) -> Unit) {
        try {
            val signedInTask = GoogleSignIn.getSignedInAccountFromIntent(intent)
            signedInTask.getResult(ApiException::class.java)?.let {
                Log.i(TAG, "登陆成功")
                resultCallBack.invoke(
                    LoginResultEntity(
                        LoginResultEntity.CODE_SUCCESS,
                        "google login success",
                        LoginResultEntity.LOGIN_TYPE_GOOGLE,
                        //token = signInAccount?.idToken,
                        third_id = it.id,
                        thirdCode = it.serverAuthCode,
                        email = it.email,
                        loginname = it.displayName,
                        avatar = it.photoUrl?.toString()
                    )
                )
            } ?: resultCallBack.invoke(
                LoginResultEntity(
                    -1,
                    "google sdk callback is null",
                    LoginResultEntity.LOGIN_TYPE_GOOGLE
                )
            )
        } catch (e: ApiException) {
            e.printStackTrace()
            resultCallBack.invoke(
                LoginResultEntity(
                    e.statusCode,
                    "code=${e.statusCode} msg=${GoogleSignInStatusCodes.getStatusCodeString(e.statusCode)}",
                    LoginResultEntity.LOGIN_TYPE_GOOGLE
                )
            )
        }
    }

    /**
     * 检查是否已登陆
     */
    private fun checkIsLogin(activity: Activity): Boolean {
        return GoogleSignIn.getLastSignedInAccount(activity) != null
    }

    /**
     * 登入google
     */
    private fun signInGoogle(activity: Activity, requestCode: Int) {
        mGoogleSignInClient?.let { activity.startActivityForResult(it.signInIntent, requestCode) } ?: run {
            Log.e(TAG, "登入 mGoogleSignInClient is null")
            init(activity)
        }
    }

    /**
     * 提示无google服务
     */
    private fun showGooglePlayError(activity: Activity, code: Int) {
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(activity)
        if (GoogleApiAvailability.getInstance().isUserResolvableError(code)) {
            GoogleApiAvailability.getInstance().getErrorDialog(activity, code, GlobalConstant.REQUEST_CODE_ERROR_DIALOG)?.show()
        } else {
            val tipMsg = GoogleApiAvailability.getInstance().getErrorString(code)
            Toast.makeText(activity, "Google Service Error: $tipMsg", Toast.LENGTH_LONG).show()
        }
    }

    fun onRelease() {
        mGoogleSignInClient = null
    }
}