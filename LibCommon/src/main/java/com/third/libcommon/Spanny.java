package com.third.libcommon;

/*
 * Copyright 2015 Pavlovsky Ivan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * @author binaryfork
 *
 * Please report any issues
 * https://github.com/binaryfork/Spanny/issues
 *
 */

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

/**
 * Spannable wrapper for simple creation of Spannable strings.
 * 富文本拼接工具
 */
public class Spanny extends SpannableStringBuilder {

    private int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

    public Spanny() {
        super("");
    }

    public Spanny(CharSequence text) {
        super(text);
    }

    public Spanny(CharSequence text, Object... spans) {
        super(text);
        for (Object span : spans) {
            setSpan(span, 0, length());
        }
    }

    public Spanny(CharSequence text, Object span) {
        super(text);
        setSpan(span, 0, text.length());
    }

    /**
     * Appends the character sequence {@code text} and spans {@code spans} over the appended part.
     *
     * @param text  the character sequence to append.
     * @param spans the object or objects to be spanned over the appended text.
     * @return this {@code Spanny}.
     */
    public Spanny append(CharSequence text, Object... spans) {
        if (text == null) return this;
        append(text);
        for (Object span : spans) {
            setSpan(span, length() - text.length(), length());
        }
        return this;
    }

    public Spanny append(CharSequence text, Object span) {
        if (text == null) return this;
        append(text);
        setSpan(span, length() - text.length(), length());
        return this;
    }

    /**
     * Add the ImageSpan to the start of the text.
     *
     * @return this {@code Spanny}.
     */
    public Spanny append(CharSequence text, ImageSpan imageSpan) {
        if (TextUtils.isEmpty(text)) {
            text = "." + text;
        }
        append(text);
        int textLen = text.length();
        int start = length() - textLen;
        int end = length();
        setSpan(imageSpan, start, end);
        return this;
    }

    /**
     * Append plain text.
     *
     * @return this {@code Spanny}.
     */
    @NonNull
    @Override
    public Spanny append(CharSequence text) {
        super.append(text);
        return this;
    }

    /**
     * Change the flag. Default is SPAN_EXCLUSIVE_EXCLUSIVE.
     * The flags determine how the span will behave when text is
     * inserted at the start or end of the span's range
     *
     * @param flag see {@link Spanned}.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * Mark the specified range of text with the specified object.
     * The flags determine how the span will behave when text is
     * inserted at the start or end of the span's range.
     */
    private void setSpan(Object span, int start, int end) {
        if (start >= end) return;
        setSpan(span, start, end, flag);
    }

    /**
     * Sets a span object to all appearances of specified text in the spannable.
     * A new instance of a span object must be provided for each iteration
     * because it can't be reused.
     *
     * @param textToSpan Case-sensitive text to span in the current spannable.
     * @param getSpan    Interface to get a span for each spanned string.
     * @return {@code Spanny}.
     */
    public Spanny findAndSpan(CharSequence textToSpan, GetSpan getSpan) {
        return findAndSpan(textToSpan, false, getSpan);
    }

    public Spanny findAndSpanOnlyFirst(CharSequence textToSpan, GetSpan getSpan) {
        return findAndSpan(textToSpan, true, getSpan);
    }

    public Spanny findAndSpan(CharSequence textToSpan, boolean isOnlyFindFirst, GetSpan getSpan) {
        if (TextUtils.isEmpty(textToSpan)) return this;
        int lastIndex = 0;
        while (lastIndex != -1 && getSpan != null) {
            lastIndex = toString().indexOf(textToSpan.toString(), lastIndex);
            if (lastIndex != -1) {
                setSpan(getSpan.getSpan(), lastIndex, lastIndex + textToSpan.length());
                lastIndex += textToSpan.length();
                if (isOnlyFindFirst) break;
            }
        }
        return this;
    }

    /**
     * Interface to return a new span object when spanning multiple parts in the text.
     */
    public interface GetSpan {
        /**
         * @return A new span object should be returned.
         */
        Object getSpan();
    }
}
