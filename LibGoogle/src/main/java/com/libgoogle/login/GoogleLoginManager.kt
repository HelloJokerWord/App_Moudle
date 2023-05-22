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
import com.libgoogle.login.entity.GoogleSignInAccountEntity


/**
 * Created on 2022/9/8.
 * @author Joker
 * Des: google登陆管理工具
 */
object GoogleLoginManager {

    private const val TAG = "GoogleLoginManager"

    @SuppressLint("StaticFieldLeak")
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private const val REQUEST_CODE_ERROR_DIALOG = 200

    /**
     * 初始化google凭证
     */
    fun init(activity: Activity) {
        val key = MetaDataUtils.getMetaDataInApp("google_login_key")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(key)
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
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
    fun signOutGoogle(listener: () -> Unit) {
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
    fun googleLoginOnResult(intent: Intent?, success: (GoogleSignInAccountEntity?) -> Unit, failed: (Int, String) -> Unit) {
        try {
            val signedInTask = GoogleSignIn.getSignedInAccountFromIntent(intent)
            signedInTask.getResult(ApiException::class.java)?.apply {
                Log.i(TAG, "登陆成功")
                success(
                    GoogleSignInAccountEntity(
                        account = account,
                        givenName = givenName,
                        serverAuthCode = serverAuthCode,
                        photoUrl = photoUrl,
                        displayName = displayName,
                        email = email,
                        id = id,
                        familyName = familyName,
                        idToken = idToken
                    )
                )
            } ?: success(null)
        } catch (e: ApiException) {
            e.printStackTrace()
            failed(e.statusCode, GoogleSignInStatusCodes.getStatusCodeString(e.statusCode))
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
            GoogleApiAvailability.getInstance().getErrorDialog(activity, code, REQUEST_CODE_ERROR_DIALOG)?.show()
        } else {
            val tipMsg = GoogleApiAvailability.getInstance().getErrorString(code)
            Toast.makeText(activity, "Google Service Error: $tipMsg", Toast.LENGTH_LONG).show()
        }
    }
}