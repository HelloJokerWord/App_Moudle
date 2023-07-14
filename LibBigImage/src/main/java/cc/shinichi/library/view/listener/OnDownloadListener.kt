package cc.shinichi.library.view.listener

import androidx.fragment.app.FragmentActivity

/**
 * @author 工藤
 * @email qinglingou@gmail.com
 * cc.shinichi.library.view.listener
 * create at 2018/12/19  16:23
 * description: 下载结果回调
 */
abstract class OnDownloadListener {
    abstract fun onDownloadStart(activity: FragmentActivity?, position: Int)
    abstract fun onDownloadSuccess(activity: FragmentActivity?, position: Int)
    abstract fun onDownloadFailed(activity: FragmentActivity?, position: Int)
}