package com.libweight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;



/**
 * 包边字TextView
 * This class adds a stroke to the generic TextView allowing the text to stand out better against
 * the background (ie. in the AllApps button).
 * <p/>
 * source from: <a href="https://android.googlesource.com/platform/packages/apps/Launcher2/+/tools_r22/src/com/android/launcher2/StrokedTextView.java">...</a>
 */
public class StrokedTextView extends AppCompatTextView {
    private final Canvas mCanvas = new Canvas();
    private final Paint mPaint = new Paint();
    private Bitmap mCache;
    private boolean mUpdateCachedBitmap;

    private int mStrokeColor;
    private float mStrokeWidth;
    private float mTextWidth;
    private int mTextColor;

    public StrokedTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public StrokedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public StrokedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokedTextView, defStyle, 0);
        mStrokeColor = a.getColor(R.styleable.StrokedTextView_StrokeColor, Color.WHITE);
        mStrokeWidth = a.getFloat(R.styleable.StrokedTextView_StrokeWidth, 0.0f);
        mTextWidth = a.getFloat(R.styleable.StrokedTextView_textWidth, 0.0f);
        mTextColor = a.getColor(R.styleable.StrokedTextView_strokeTextColor, Color.BLACK);
        a.recycle();
        mUpdateCachedBitmap = true;
        // Setup the text paint
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        // 把字体方向设置成LTR , 因为此view只用于倒数和combo， 如后面有其他复杂用途需要onDraw里重算
        setTextDirection(TextView.TEXT_DIRECTION_LTR);
    }

    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        mUpdateCachedBitmap = true;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mUpdateCachedBitmap = true;
            mCache = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        } else {
            mCache = null;
        }
    }

    protected void onDraw(Canvas canvas) {
        if (mCache != null) {
            if (mUpdateCachedBitmap) {
                final int w = getMeasuredWidth();
                final int h = getMeasuredHeight();
                final String text = getText().toString();
                final Rect textBounds = new Rect();
                final Paint textPaint = getPaint();
                final int textWidth = (int) textPaint.measureText(text);
                textPaint.getTextBounds("x", 0, 1, textBounds);
                // Clear the old cached image
                mCanvas.setBitmap(mCache);
                mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                // Draw the drawable
                //final int drawableLeft = getPaddingLeft();
                final int drawableLeft = getPaddingStart();
                final int drawableTop = getPaddingTop();
                final Drawable[] drawables = getCompoundDrawables();
                for (Drawable drawable : drawables) {
                    if (drawable != null) {
                        drawable.setBounds(drawableLeft, drawableTop,
                                drawableLeft + drawable.getIntrinsicWidth(),
                                drawableTop + drawable.getIntrinsicHeight());
                        drawable.draw(mCanvas);
                    }
                }
                //final int left = w - getPaddingRight() - textWidth+2;
                final int left = w - getPaddingEnd() - textWidth + 2;
                final int bottom = (h + textBounds.height()) / 2;
                // Draw the outline of the text
                mPaint.setStrokeWidth(mStrokeWidth);
                mPaint.setColor(mStrokeColor);
                mPaint.setFakeBoldText(true);
                mPaint.setTextSize(getTextSize() - 3f);
                mCanvas.drawText(text, left, bottom, mPaint);
                // Draw the text itself
                mPaint.setStrokeWidth(mTextWidth);
                mPaint.setColor(mTextColor);
                mPaint.setFakeBoldText(true);//设置加粗
                mPaint.setShadowLayer(2, 2, 2, 0xCCCCCC);
                mCanvas.drawText(text, left, bottom, mPaint);
                mUpdateCachedBitmap = false;
            }
            canvas.drawBitmap(mCache, 0, 0, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setStrokeColor(int mStrokeColor) {
        this.mStrokeColor = mStrokeColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }
}