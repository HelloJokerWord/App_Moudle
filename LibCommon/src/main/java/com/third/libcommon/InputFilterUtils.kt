package com.third.libcommon

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * CreateBy:Joker
 * CreateTime:2023/6/23 14:18
 * description：
 */

object InputFilterUtils {

    /**
     * 过滤emoji表情
     * @return InputFilter
     */
    fun getInputFilterEmoji() = getInputFilter(isEmojiCharacter)

    /**
     * 过滤回车或换行符
     * @return InputFilter
     */
    fun getInputFilterNR() = getInputFilter(isNRCharacter)

    /**
     * 过滤掉非法字符
     * @return InputFilter
     */
    fun getInputFilterCharToNormal() = getInputFilter(isNRCharacter)


    private fun getInputFilter(isFilterCharacter: (Char) -> Boolean): InputFilter {
        return InputFilter { source, start, end, dest, dstart, dend ->
            val buffer = StringBuffer()
            var i = start
            while (i < end) {
                val codePoint = source[i]
                if (!isFilterCharacter(codePoint)) {
                    buffer.append(codePoint)
                }
                i++
            }

            if (source is Spanned) {
                val sp = SpannableString(buffer)
                TextUtils.copySpansFrom(source, start, end, null, sp, 0)
                sp
            } else {
                buffer
            }
        }
    }

    //Emoji区间
    private val isEmojiCharacter: (Char) -> Boolean = { char ->
        val codePoint = char.code
        !(codePoint == 0x0
                || codePoint == 0x9
                || codePoint == 0xA
                || codePoint == 0xD
                || codePoint in 0x20..0xD7FF
                || codePoint in 0xE000..0xFFFD
                || codePoint in 0x10000..0x10FFFF)
    }

    //回车或换行符
    private val isNRCharacter: (Char) -> Boolean = { char ->
        val filterChars = charArrayOf('\n', '\r')
        var result = false
        for (filterChar in filterChars) {
            if (filterChar == char) {
                result = true
                break
            }
        }
        result
    }

    // 汉字、数字、大小写字母区间
    private val isCharToNormalCharacter: (Char) -> Boolean = { char ->
        val codePoint = char.code
        (codePoint in 0x4e00..0x9fa5 ||     //表示汉字区间
                codePoint in 0x30..0x39 ||  //表示数字区间
                codePoint in 0x41..0x5a ||  //表示大写字母区间
                codePoint in 0x61..0x7a)    //表示小写字母区间
    }
}

// 最大英文/数字长度 一个汉字算两个字母
class NameLengthFilter(var MAX_EN: Int) : InputFilter {
    var regEx = "[\\u4e00-\\u9fa5]" // unicode编码，判断是否为汉字

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val destCount = dest.toString().length + getChineseCount(dest.toString())
        val sourceCount = (source.toString().length + getChineseCount(source.toString()))
        return if (destCount + sourceCount > MAX_EN) "" else source
    }

    private fun getChineseCount(str: String): Int {
        var count = 0
        val p: Pattern = Pattern.compile(regEx)
        val m: Matcher = p.matcher(str)
        while (m.find()) {
            for (i in 0..m.groupCount()) {
                count += 1
            }
        }
        return count
    }
}