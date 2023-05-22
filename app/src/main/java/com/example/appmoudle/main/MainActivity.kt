package com.example.appmoudle.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.example.appmoudle.R
import com.example.appmoudle.base.BaseSupportActivity
import com.example.appmoudle.config.EventKeyBoardChange
import com.example.appmoudle.config.EventNetWorkChange
import com.example.appmoudle.config.GlobalUserManager
import com.third.libcommon.mmkv.MMKVKey
import com.example.appmoudle.databinding.ActivityMainBinding
import com.example.appmoudle.dialog.AppConfirmPopup
import com.example.appmoudle.login.LoginF
import com.third.libcommon.PermissionManager
import com.lxj.xpopup.core.BasePopupView
import com.third.libcommon.LiveEventManager
import com.third.libcommon.mmkv.MMKVManager
import com.third.libcommon.WeakHandler

class MainActivity : BaseSupportActivity<ActivityMainBinding>() {

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    companion object {
        var mainActivity: MainActivity? = null
        var isMainActivityAlive = false
    }

    //网络变化设置回调
    private val netWorkChangeListener by lazy {
        object : NetworkUtils.OnNetworkStatusChangedListener {
            override fun onDisconnected() {
                Log.e(TAG, "onDisconnected")
                LiveEventManager.post(EventNetWorkChange(false, "NoNetWork"))
            }

            override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                Log.i(TAG, "onConnected ${networkType?.name}-${networkType?.ordinal}")
                LiveEventManager.post(EventNetWorkChange(true, "${networkType?.name}-${networkType?.ordinal}"))
            }
        }
    }

    //app前后台变化回调
    private val appStatusChange by lazy {
        object : Utils.OnAppStatusChangedListener {
            override fun onForeground(activity: Activity?) {
                Log.i(TAG, "onForeground")
                if (PermissionManager.isJumpFromSettingPage) {
                    checkAppPermission()
                    PermissionManager.isJumpFromSettingPage = false
                }
            }

            override fun onBackground(activity: Activity?) {
                Log.i(TAG, "onBackground")
            }
        }
    }


    private var settingDialog: BasePopupView? = null
    private var lastBackPressedTime = 0L
    private val weakHandler = WeakHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.i("onCreate")
        isMainActivityAlive = true
        KeyboardUtils.fixAndroidBug5497(this)
        checkAppPermission()
        //注册键盘弹出
        initKeyboard()
        //处理深度链接
        dealWithDeeplink(intent, true)
        //注册APP前后台变化
        AppUtils.registerAppStatusChangedListener(appStatusChange)
        //注册网络变化
        NetworkUtils.registerNetworkStatusChangedListener(netWorkChangeListener)
        //预加载资源
        initResDownload()
    }

    //APP活着的时候回来APP调该方法
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { dealWithDeeplink(it, false) }
    }

    override fun onResume() {
        super.onResume()
        mainActivity = this
    }

    override fun onDestroy() {
        weakHandler.removeCallbacksAndMessages(null)
        NetworkUtils.unregisterNetworkStatusChangedListener(netWorkChangeListener)
        AppUtils.unregisterAppStatusChangedListener(appStatusChange)
        dismissSettingDialog()
        super.onDestroy()
    }

    override fun onBackPressedSupport() {
        //2秒内 连续两次按返回键才退出
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressedTime > 2000L) {
            lastBackPressedTime = currentTime
            ToastUtils.showShort("${StringUtils.getString(R.string.hc_exit_app_one_more_time)}${StringUtils.getString(R.string.app_name)}")
        } else {
            super.onBackPressedSupport()
        }
    }

    /**
     * 检查app所需必要权限
     */
    private fun checkAppPermission() {
        // 版本大于6.0动态申请权限
        PermissionManager.checkStoragePermission(this, {
            loadRootFragment(R.id.flContainerView, if (GlobalUserManager.isLogin()) MainF.newInstance() else LoginF.newInstance())
        }, { _, _, _ -> showSettingDialog() })
    }

    /**
     * 登陆成功，替换最底层页面为main页面
     */
    fun showMainPage() {
        loadRootFragment(R.id.flContainerView, MainF.newInstance())
    }

    /**
     *  回退到登陆页面
     */
    fun backToLoginPage() {
        loadRootFragment(R.id.flContainerView, LoginF.newInstance())
    }

    /**
     * 提示给予权限
     */
    private fun showSettingDialog() {
        dismissSettingDialog()
        settingDialog = AppConfirmPopup.Builder(this)
            .setContent(StringUtils.getString(R.string.hc_default_permission_denied_forever_warning))
            .setSubmitText(StringUtils.getString(R.string.hc_word_confirm))
            .setCancelText(StringUtils.getString(R.string.hc_word_cancel))
            .setOnClickSubmitListener {
                PermissionManager.isJumpFromSettingPage = true
                PermissionUtils.launchAppDetailsSettings()
            }
            .setOnClickCancelListener { finish() }
            .build()
            .show()
    }

    private fun dismissSettingDialog() {
        settingDialog?.dismiss()
        settingDialog = null
    }

    /**
     * 处理通知栏打开app
     */
    private fun dealWithDeeplink(intent: Intent, isFromOnCreate: Boolean) {

    }

    private fun initKeyboard() {
        KeyboardUtils.registerSoftInputChangedListener(this) { height ->
            Log.i(TAG, "键盘高度=$height")
            //更新键盘高度
            if (MMKVManager.getInt(MMKVKey.KEYBOARD_HIGH) != height && height > 0) {
                MMKVManager.put(MMKVKey.KEYBOARD_HIGH, height)
            }
            LiveEventManager.post(EventKeyBoardChange(height))
        }
    }


    private fun initResDownload() {

    }
}