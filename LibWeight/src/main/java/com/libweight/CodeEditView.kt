package com.libweight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.*
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils

/**
 * 描述: 验证码输入框  https://github.com/lvkaixuan
 */
class CodeEditView : LinearLayout, TextWatcher, View.OnClickListener {

    private var editViewNum = 4                         //默认输入框数量
    private val mTextViewsList = ArrayList<TextView>()  //存储EditText对象
    private var mContext: Context? = null
    private var mEditText: EditText? = null
    private var borderSize = 35                         //方格边框大小
    private var borderMargin = 10                       //方格间距
    private var textSize = 8                            //字体大小
    private var textColor = 0xff                        //字体颜色
    private val inputType = InputType.TYPE_CLASS_NUMBER

    var callBack: InputEndListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initData(context, attrs)
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initData(context, attrs)
        init(context)
    }

    private fun initData(context: Context, attrs: AttributeSet?) {
        @SuppressLint("Recycle")
        val array = context.obtainStyledAttributes(attrs, R.styleable.CodeEditView)
        borderSize = array.getInteger(R.styleable.CodeEditView_borderSize, 35)
        borderMargin = array.getInteger(R.styleable.CodeEditView_borderMargin, 10)
        textSize = array.getInteger(R.styleable.CodeEditView_textSize, 8)
        textColor = array.getColor(R.styleable.CodeEditView_textColor, Color.BLACK)
        editViewNum = array.getInteger(R.styleable.CodeEditView_borderNum, 6)
    }

    private fun init(context: Context) {
        mContext = context
        initEditText(context)
        //设置方格间距
        val params = LayoutParams(ConvertUtils.dp2px(borderSize.toFloat()), ConvertUtils.dp2px(borderSize.toFloat()))
        params.setMargins(ConvertUtils.dp2px(borderMargin.toFloat()), 0, 0, 0)

        val params1 = LayoutParams(ConvertUtils.dp2px(borderSize.toFloat()), ConvertUtils.dp2px(borderSize.toFloat()))
        params1.setMargins(0, 0, 0, 0)
        //设置方格文字
        for (i in 0 until editViewNum) {
            TextView(mContext).apply {
                setBackgroundResource(R.drawable.weight_shape_border_normal)
                gravity = Gravity.CENTER
                textSize = ConvertUtils.sp2px(this@CodeEditView.textSize.toFloat()).toFloat()
                paint.isFakeBoldText = true
                layoutParams = if (i == 0) params1 else params
                inputType = inputType
                setTextColor(textColor)
                setOnClickListener(this@CodeEditView)
                mTextViewsList.add(this)
                addView(this)
            }
        }
    }

    interface InputEndListener {
        fun inputFinish(text: String?)
        fun afterTextChanged(text: String?)
    }

    private fun initEditText(context: Context) {
        mEditText = EditText(context).apply {
            setBackgroundColor(Color.parseColor("#00000000"))
            maxLines = 1
            inputType = this@CodeEditView.inputType
            filters = arrayOf<InputFilter>(LengthFilter(editViewNum))
            addTextChangedListener(this@CodeEditView)
            textSize = 0f
            height = 1
            width = 1
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }

        addView(mEditText)
    }

    //清空文字
    fun clearText() {
        mEditText?.setText("")
        for (textView in mTextViewsList) {
            textView.text = ""
        }
    }

    fun destroy() {
        callBack = null
    }

    //TextView点击时获取焦点弹出输入法
    override fun onClick(v: View) {
        mEditText?.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        KeyboardUtils.toggleSoftInput()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        if (s == null) return
        callBack?.afterTextChanged(s.toString())
        try {
            val sL = s.toMutableList()
            val list = mutableListOf<String>()
            sL.forEach { list.add(it.toString()) }
            repeat(editViewNum - sL.size) { list.add("") }
            list.forEachIndexed { index, value -> mTextViewsList[index].text = value }

            if (s.length == editViewNum) {
                callBack?.inputFinish(mEditText?.text.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}