package com.libweight

import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatEditText

/**
 * CreateBy:Joker
 * CreateTime:2023/6/29 12:17
 * description：
 */
class MaEditText : AppCompatEditText, MenuItem.OnMenuItemClickListener {

    companion object {
        private const val TAG = "MyEditText"
    }

    private var mContext: Context? = null
    var pasteListener: ((str: String) -> Unit)? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return onTextContextMenuItem(item.itemId)
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        val consumed = super.onTextContextMenuItem(id)
        // 监听
        when (id) {
            android.R.id.cut -> onTextCut()
            android.R.id.paste -> onTextPaste()
            android.R.id.copy -> onTextCopy()
        }
        return consumed
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    /**
     * 剪贴操作
     */
    private fun onTextCut() {
        Log.i(TAG, "剪贴!")
    }

    /**
     * 拷贝操作
     */
    private fun onTextCopy() {
        Log.i(TAG, "拷贝!")
    }

    /**
     * 粘贴操作
     */
    private fun onTextPaste() {
        try {
            val clipboardManager = mContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            //改变剪贴板中Content
            clipboardManager?.primaryClip?.getItemAt(0)?.text?.let {
                Log.i(TAG, "粘贴内容=$it")
                if (it.isEmpty()) return
                pasteListener?.invoke(it.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}