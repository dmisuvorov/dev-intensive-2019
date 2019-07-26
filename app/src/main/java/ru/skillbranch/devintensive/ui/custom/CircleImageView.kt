package ru.skillbranch.devintensive.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils

class CircleImageView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ImageView(context, attrs, defStyleAttrs) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2

        private const val COLOR_DRAWABLE_DIMENSION = 2
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    }

    var text: String = ""
        set(value){
            field = computeText(value)
        }

    private val textPaint = Paint()
    private lateinit var textDrawable: Drawable

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private var bitmapWidth: Int = 0
    private var bitmapHeight: Int = 0
    private var bitmap: Bitmap? = null

    private lateinit var bitmapShader: BitmapShader

    private val bitmapPaint = Paint()
    private val borderPaint = Paint()
    private val borderRect = RectF()
    private val drawableRect = RectF()
    private val shaderMatrix = Matrix()

    private var borderRadius: Float = 0f
    private var drawableRadius: Float = 0f


    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            a.recycle()
            setup()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        bitmap ?: return
        canvas?.drawCircle(drawableRect.centerX(), drawableRect.centerY(), drawableRadius, bitmapPaint)
        if (borderWidth > 0) {
            canvas?.drawCircle(borderRect.centerX(), borderRect.centerY(), borderRadius, borderPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }


    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    fun getBorderWidth(): Int = borderWidth

    fun setBorderWidth(dp: Int) {
        if (borderWidth == dp) return else borderWidth = dp
        setup()
    }

    fun getBorderColor(): Int = borderColor


    @SuppressLint("ResourceType")
    fun setBorderColor(hex: String) {
        setBorderColor(Color.parseColor(hex))
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        if (borderColor == colorId) return else borderColor = colorId
        borderPaint.color = borderColor
        invalidate()
    }

    private fun setup() {
        if (width == 0 && height == 0) return
        if (bitmap == null) {
            invalidate()
            return
        }

        bitmapShader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        bitmapPaint.isAntiAlias = true
        bitmapPaint.shader = bitmapShader

        borderPaint.style = Paint.Style.STROKE
        borderPaint.isAntiAlias = true
        borderPaint.color = borderColor
        borderPaint.strokeWidth = borderWidth.toFloat()

        bitmapHeight = bitmap!!.height
        bitmapWidth = bitmap!!.width

        borderRect.set(calculateBounds())
        borderRadius = Math.min((borderRect.height() - borderWidth) / 2.0f, (borderRect.width() - borderWidth) / 2.0f)

        drawableRect.set(borderRect)

        drawableRadius = Math.min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f)
        updateShaderMatrix()
        invalidate()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom

        val sideLength = Math.min(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f

        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun initializeBitmap() {
        //TODO: if text not null getBitmapFromDrawable(Drawabletext)
        bitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        drawable ?: return null
        if (drawable is BitmapDrawable) return drawable.bitmap

        return try {
            val bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0.0f
        var dy = 0.0f

        shaderMatrix.set(null)
        if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
            scale = drawableRect.height() / bitmapHeight.toFloat()
            dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = drawableRect.width() / bitmapWidth.toFloat()
            dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate((dx + 0.5f).toInt() + drawableRect.left, (dy + 0.5f).toInt() + drawableRect.top)

        bitmapShader.setLocalMatrix(shaderMatrix)
    }

    private fun drawText(canvas: Canvas?) {

    }

    private fun initTextDrawable() {
        textDrawable = object : ShapeDrawable() {
            override fun draw(canvas: Canvas) {
                super.draw(canvas)
                val a = TypedValue()
                val typedArray = context.obtainStyledAttributes(a.data, intArrayOf(R.attr.colorAccent))
                textPaint.color = typedArray.getColor(0, 0)
                typedArray.recycle()
                textPaint.isAntiAlias = true
                textPaint.style = Paint.Style.FILL
                textPaint.typeface = Typeface.DEFAULT
                textPaint.textAlign = Paint.Align.CENTER

                val rect = textDrawable.bounds

                val count = canvas.save()
                canvas.translate(rect.left.toFloat(), rect.top.toFloat())

                val width = rect.width()
                val height = rect.height()
                val fontSize = Math.min(width, height) / 2
                textPaint.textSize = fontSize.toFloat()
                canvas.drawText(text, (width / 2).toFloat(), height / 2 - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint)
                canvas.restoreToCount(count)
            }
        }
    }

    private fun computeText(string: String): String =
        if (string.isEmpty()) string
            else {
                val initials = string.trim().split("\\s+")
                Utils.toInitials(initials[0], if (initials.size > 1) initials[1] else null)!!
            }
}
