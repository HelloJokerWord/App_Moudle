# 工程相关介绍

### 一、基本的页面开发

|             模版类名              |        模板介绍         |
|:-----------------------------:|:-------------------:|
|            SimpleF            |      普通页面开发模版       |
|          SimpleReqF           |      带请求页面开发模板      |
|        SimpleViewModel        |     数据请求处理中心模版      |
|      SimpleQuickAdapter       |     单布局列表适配器模板      |
|      SimpleMultiAdapter       |     多部剧列表适配器模板      |
|      SimpleBottomDialog       |       底部弹窗模板        |
|      SimpleCenterDialog       |       中间弹窗模板        |
|     SimplePositionDialog      | 自定义自由定位Position弹窗模版 |
|          CommonWebF           |     启动全屏web页面模版     |
|     CommonBottomWebDialog     |    启动底部web页面弹窗模板    |
|      CommonLoadingDialog      |   启动中间loading弹窗模板   |
| layout_simple_refresh_loading |       刷新加载模板        |

### 二、基本全局类使用

|        类名         |       介绍        |
|:-----------------:|:---------------:|
|  GlobalConstant   |    全局App常量池     |
|    GlobalPath     |    全局App路径地址    |
| GlobalUserManager |    全局用户信息管理类    |
|  GlobalLiveEvent  | 全局Eventbus通知管理类 |
|       JsCMD       | 全局JS调原生关键字常量池子  |
|      WebURL       |    全局web页面地址    |
|      URLApi       |    全局网络请求地址     |
|     SocketURL     |  全局socket配置地址   |
| EnvSwitchManager  |    全局环境配置管理     |

### 三、常用扩展函数

- 数据解析

  fromJson
  toJson

- 多个数据判空

  safeLet

### 四、数据存储

- 少量数据存储

      MMKVManager，key值放在MMKVKey种保存

- objectbox数据库存储

      DBManager             //数据库管理
      TraceDBEntity         //业务数据库实体类模版
      TraceDB               //业务数据库操作模版
      TraceHelp             //业务数据库对外使用模版

### 五、网络请求

    //网络请求管理类	 HttpManager			
    内部包含get post 文件上传下载方法，以及okhttp配置 和 请求公参配置等
    配合livedata使用，可参考SimpleViewModel使用

### 六、倒计时定时器工具

```kotlin
/**
 * 倒计时定时器 CountDownTimerSupport
 * 启动倒计时
 */
private fun startCountDown() {
    val countDown = CountDownTimerSupport(sleepTime * TimeConstants.SEC.toLong(), TimeConstants.SEC.toLong())
    countDown?.setOnCountDownTimerListener(object : OnCountDownTimerListener {
        override fun onTick(millisUntilFinished: Long) {
            mViewBinding?.apply {
                leftTime = (millisUntilFinished / 1000).toInt()
                pbSayHello.progress = sleepTime - leftTime
                tvSayHelloSec.text = "${leftTime}s"
            }
        }

        override fun onFinish() {
            leftTime = 0
            mViewBinding?.groupProgress?.isVisible = false
            sayHelloViewModel.checkSayHelloConfig()
        }
    })
    countDown?.start()
}

/**
 * 关闭倒计时
 */
private fun stopCountDownTime() {
    countDown?.stop()
    countDown = null
}
```

### 七、其他工具

|           类名           |                        介绍                         |
|:----------------------:|:-------------------------------------------------:|
|    LiveEventManager    |                       通知推送                        |
|    BusinessManager     |  socket管理，启动关闭发送等 ， 接收通知，参考BusinessSocketClient   |
|       LogManager       | 日志配置开关和上传下载，使用com.blankj.utilcode.util.LogUtils记录 |
|      LubanManager      |                  图片压缩工具，配合图片选择使用                  |
|   PermissionManager    |                     动态权限申请工具                      |
|         Spanny         |                      富文本拼接工具                      |
|      WeakHandler       |                 防止内存泄漏handler，弱引用                 |
|  ImagePreviewManager   |                      大图浏览工具                       |
|       FCMManager       |                     fcm推送管理类                      |
|    GooglePayManager    |                    google支付管理类                    |
|  PhotosSelectManager   |                      相册选取工具类                      |
|      GlideManager      |                     图片下载显示工具                      |
|   ImageUploadManager   |                     图片选择上传工具                      |
|    StateViewManager    |                       状态工具                        |
|     CommonStartup      |                     App启动初始化                      |
|    AppsFlyerManager    |                      数据归因管理类                      |
|   GoogleLoginManager   |                    google登陆管理类                    |
|  FacebookLoginManager  |                   facebook登陆管理类                   |
| GoogleAppUpdateManager |                  google应用内升级管理类                   |

### 八、其他介绍

|          文件名           |      介绍       |
|:----------------------:|:-------------:|
|     config.gradle      |    工程配置文件     |
| jenkins_package.gradle | jenkins打包配置文件 |


















