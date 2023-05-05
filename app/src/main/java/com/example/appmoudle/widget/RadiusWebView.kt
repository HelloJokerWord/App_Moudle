package com.example.appmoudle.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.webkit.WebView

/**
 * Created on 2022/7/21.
 *
 * @author Joker
 * Des: 支持圆角WebView
 */
class RadiusWebView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WebView(context, attrs, defStyleAttr) {

    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f

    private var vWidth = 0
    private var vHeight = 0
    private var x = 0
    private var y = 0
    private var paint1: Paint? = null
    private var paint2: Paint? = null

    private fun init(context: Context) {
        paint1 = Paint()
        paint1?.color = Color.WHITE
        paint1?.isAntiAlias = true
        paint1?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        paint2 = Paint()
        paint2?.xfermode = null
    }

    /**
     * 设置四个角度
     */
    fun setRadius(radius: Float) {
        topLeftRadius = radius
        topRightRadius = radius
        bottomLeftRadius = radius
        bottomRightRadius = radius
    }

    /**
     * 设置顶部圆角
     */
    fun setTopRadius(radius: Float) {
        topLeftRadius = radius
        topRightRadius = radius
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        vWidth = measuredWidth
        vHeight = measuredHeight
    }

    override fun draw(canvas: Canvas) {
        x = this.scrollX
        y = this.scrollY
        val bitmap = Bitmap.createBitmap(
            x + vWidth, y + vHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas2 = Canvas(bitmap)
        super.draw(canvas2)
        drawLeftUp(canvas2)
        drawRightUp(canvas2)
        drawLeftDown(canvas2)
        drawRightDown(canvas2)
        canvas.drawBitmap(bitmap, 0f, 0f, paint2)
        bitmap.recycle()
    }

    /**
     * 处理左上角
     */
    private fun drawLeftUp(canvas: Canvas) {
        val path = Path()
        path.moveTo(x.toFloat(), topLeftRadius)
        path.lineTo(x.toFloat(), y.toFloat())
        path.lineTo(topLeftRadius, y.toFloat())
        path.arcTo(RectF(x.toFloat(), y.toFloat(), x + topLeftRadius * 2, y + topLeftRadius * 2), -90f, -90f)
        path.close()
        paint1?.let { canvas.drawPath(path, it) }
    }

    /**
     * 处理左下角
     */
    private fun drawLeftDown(canvas: Canvas) {
        val path = Path()
        path.moveTo(x.toFloat(), y + vHeight - bottomLeftRadius)
        path.lineTo(x.toFloat(), (y + vHeight).toFloat())
        path.lineTo(x + bottomLeftRadius, (y + vHeight).toFloat())
        path.arcTo(RectF(x.toFloat(), y + vHeight - bottomLeftRadius * 2, x + bottomLeftRadius * 2, (y + vHeight).toFloat()), 90f, 90f)
        path.close()
        paint1?.let { canvas.drawPath(path, it) }
    }

    /**
     * 处理右下角
     */
    private fun drawRightDown(canvas: Canvas) {
        val path = Path()
        path.moveTo(x + vWidth - bottomRightRadius, (y + vHeight).toFloat())
        path.lineTo((x + vWidth).toFloat(), (y + vHeight).toFloat())
        path.lineTo((x + vWidth).toFloat(), y + vHeight - bottomRightRadius)
        path.arcTo(RectF(x + vWidth - bottomRightRadius * 2, y + vHeight - bottomRightRadius * 2, (x + vWidth).toFloat(), (y + vHeight).toFloat()), 0f, 90f)
        path.close()
        paint1?.let { canvas.drawPath(path, it) }
    }

    /**
     * 处理右上角
     */
    private fun drawRightUp(canvas: Canvas) {
        val path = Path()
        path.moveTo((x + vWidth).toFloat(), y + topRightRadius)
        path.lineTo((x + vWidth).toFloat(), y.toFloat())
        path.lineTo(x + vWidth - topRightRadius, y.toFloat())
        path.arcTo(RectF(x + vWidth - topRightRadius * 2, y.toFloat(), (x + vWidth).toFloat(), y + topRightRadius * 2), -90f, 90f)
        path.close()
        paint1?.let { canvas.drawPath(path, it) }
    }

    init {
        init(context)
    }
}