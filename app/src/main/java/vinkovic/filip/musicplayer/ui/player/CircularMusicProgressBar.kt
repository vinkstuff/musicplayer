package vinkovic.filip.musicplayer.ui.player

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.widget.ImageView
import info.abdolahi.circularmusicbar.R.styleable

class CircularMusicProgressBar : ImageView {
    private val mDrawableRect: RectF
    private val mBorderRect: RectF
    private val mShaderMatrix: Matrix
    private val mBitmapPaint: Paint?
    private val mBorderPaint: Paint
    private val mFillPaint: Paint
    private var mBorderColor: Int = 0
    private var mBorderWidth: Int = 0
    private var mFillColor: Int = 0
    private var mProgressColor: Int = 0
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth: Int = 0
    private var mBitmapHeight: Int = 0
    private var mInnrCircleDiammeter: Float = 0.toFloat()
    private var mDrawableRadius: Float = 0.toFloat()
    private var mBorderRadius: Float = 0.toFloat()
    private var mProgressValue: Float = 0.toFloat()
    private var mColorFilter: ColorFilter? = null
    private var mReady: Boolean = false
    private var mSetupPending: Boolean = false
    private var mBorderOverlay: Boolean = false
    var isDisableCircularTransformation: Boolean = false
        set(disableCircularTransformation) {
            if (this.isDisableCircularTransformation != disableCircularTransformation) {
                field = disableCircularTransformation
                this.initializeBitmap()
            }
        }
    internal var mBaseStartAngle: Float = 0.toFloat()

    constructor(context: Context) : super(context) {
        this.mDrawableRect = RectF()
        this.mBorderRect = RectF()
        this.mShaderMatrix = Matrix()
        this.mBitmapPaint = Paint(1)
        this.mBorderPaint = Paint(1)
        this.mFillPaint = Paint(1)
        this.mBorderColor = -16777216
        this.mBorderWidth = 0
        this.mFillColor = 0
        this.mProgressColor = -16776961
        this.mProgressValue = 0.0f
        this.mBaseStartAngle = 0.0f
        this.init()
    }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {
        this.mDrawableRect = RectF()
        this.mBorderRect = RectF()
        this.mShaderMatrix = Matrix()
        this.mBitmapPaint = Paint(1)
        this.mBorderPaint = Paint(1)
        this.mFillPaint = Paint(1)
        this.mBorderColor = -16777216
        this.mBorderWidth = 0
        this.mFillColor = 0
        this.mProgressColor = -16776961
        this.mProgressValue = 0.0f
        this.mBaseStartAngle = 0.0f
        val a = context.obtainStyledAttributes(attrs, styleable.CircularMusicProgressBar, defStyle, 0)
        this.mBorderWidth = a.getDimensionPixelSize(styleable.CircularMusicProgressBar_border_width, 0)
        this.mBorderColor = a.getColor(styleable.CircularMusicProgressBar_border_color, -16777216)
        this.mBorderOverlay = a.getBoolean(styleable.CircularMusicProgressBar_border_overlay, false)
        this.mFillColor = a.getColor(styleable.CircularMusicProgressBar_fill_color, 0)
        this.mInnrCircleDiammeter = a.getFloat(styleable.CircularMusicProgressBar_centercircle_diammterer, DEFAULT_INNTER_DAIMMETER_FRACTION)
        this.mProgressColor = a.getColor(styleable.CircularMusicProgressBar_progress_color, -16776961)
        this.mBaseStartAngle = a.getFloat(styleable.CircularMusicProgressBar_progress_startAngle, 0.0f)
        a.recycle()
        this.init()
    }

    private fun init() {
        super.setScaleType(SCALE_TYPE)
        this.mReady = true
        if (this.mSetupPending) {
            this.setup()
            this.mSetupPending = false
        }

    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds not supported.")
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (this.isDisableCircularTransformation) {
            super.onDraw(canvas)
        } else if (this.mBitmap != null) {
            canvas.save()
            canvas.rotate(this.mBaseStartAngle, this.mDrawableRect.centerX(), this.mDrawableRect.centerY())
            mBorderPaint.strokeWidth = this.mBorderWidth.toFloat()
            if (this.mBorderWidth > 0) {
                this.mBorderPaint.color = this.mBorderColor
                canvas.drawArc(this.mBorderRect, 0.0f, 360.0f, false, this.mBorderPaint)
            }

            this.mBorderPaint.color = this.mProgressColor
            val sweepAngle = this.mProgressValue / 100.0f * 360.0f
            canvas.drawArc(this.mBorderRect, 0.0f, sweepAngle, false, this.mBorderPaint)

            canvas.restore()
            canvas.drawCircle(this.mDrawableRect.centerX(), this.mDrawableRect.centerY(), this.mDrawableRadius, this.mBitmapPaint!!)
            if (this.mFillColor != 0) {
                canvas.drawCircle(this.mDrawableRect.centerX(), this.mDrawableRect.centerY(), this.mDrawableRadius, this.mFillPaint)
            }

        }
    }

    fun setValue(newValue: Float) {
        this.mProgressValue = newValue
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        this.setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        this.setup()
    }

    var borderColor: Int
        get() = this.mBorderColor
        set(@ColorInt borderColor) {
            if (borderColor != this.mBorderColor) {
                this.mBorderColor = borderColor
                this.mBorderPaint.color = this.mBorderColor
                this.invalidate()
            }
        }

    fun setBorderProgressColor(@ColorInt borderColor: Int) {
        if (borderColor != this.mProgressColor) {
            this.mProgressColor = borderColor
            this.invalidate()
        }
    }


    @Deprecated("")
    fun setBorderColorResource(@ColorRes borderColorRes: Int) {
        this.borderColor = this.context.resources.getColor(borderColorRes)
    }

    @Deprecated("")
    var fillColor: Int
        get() = this.mFillColor
        set(@ColorInt fillColor) {
            if (fillColor != this.mFillColor) {
                this.mFillColor = fillColor
                this.mFillPaint.color = fillColor
                this.invalidate()
            }
        }


    @Deprecated("")
    fun setFillColorResource(@ColorRes fillColorRes: Int) {
        this.fillColor = this.context.resources.getColor(fillColorRes)
    }

    var borderWidth: Int
        get() = this.mBorderWidth
        set(borderWidth) {
            if (borderWidth != this.mBorderWidth) {
                this.mBorderWidth = borderWidth
                this.setup()
            }
        }

    var isBorderOverlay: Boolean
        get() = this.mBorderOverlay
        set(borderOverlay) {
            if (borderOverlay != this.mBorderOverlay) {
                this.mBorderOverlay = borderOverlay
                this.setup()
            }
        }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        this.initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        this.initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        this.initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        this.initializeBitmap()
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf !== this.mColorFilter) {
            this.mColorFilter = cf
            this.applyColorFilter()
            this.invalidate()
        }
    }

    override fun getColorFilter(): ColorFilter? {
        return this.mColorFilter
    }

    private fun applyColorFilter() {
        if (this.mBitmapPaint != null) {
            this.mBitmapPaint.colorFilter = this.mColorFilter
        }

    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        } else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        } else {
            try {
                val e: Bitmap
                if (drawable is ColorDrawable) {
                    e = Bitmap.createBitmap(2, 2, BITMAP_CONFIG)
                } else {
                    e = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
                }

                val canvas = Canvas(e)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return e
            } catch (var4: Exception) {
                var4.printStackTrace()
                return null
            }

        }
    }

    private fun initializeBitmap() {
        if (this.isDisableCircularTransformation) {
            this.mBitmap = null
        } else {
            this.mBitmap = this.getBitmapFromDrawable(this.drawable)
        }

        this.setup()
    }

    private fun setup() {
        if (!this.mReady) {
            this.mSetupPending = true
        } else if (this.width != 0 || this.height != 0) {
            if (this.mBitmap == null) {
                this.invalidate()
            } else {
                this.mBitmapShader = BitmapShader(this.mBitmap!!, TileMode.CLAMP, TileMode.CLAMP)
                this.mBitmapPaint!!.isAntiAlias = true
                this.mBitmapPaint.shader = this.mBitmapShader
                this.mBorderPaint.style = Style.STROKE
                this.mBorderPaint.isAntiAlias = true
                this.mBorderPaint.color = this.mBorderColor
                this.mBorderPaint.strokeWidth = this.mBorderWidth.toFloat()
                this.mBorderPaint.strokeCap = Cap.ROUND
                this.mFillPaint.style = Style.FILL
                this.mFillPaint.isAntiAlias = true
                this.mFillPaint.color = this.mFillColor
                this.mBitmapHeight = this.mBitmap!!.height
                this.mBitmapWidth = this.mBitmap!!.width
                this.mBorderRect.set(this.calculateBounds())
                this.mBorderRadius = Math.min((this.mBorderRect.height() - this.mBorderWidth.toFloat()) / 2.0f, (this.mBorderRect.width() - this.mBorderWidth.toFloat()) / 2.0f)
                this.mDrawableRect.set(this.mBorderRect)
                if (!this.mBorderOverlay && this.mBorderWidth > 0) {
                    this.mDrawableRect.inset(this.mBorderWidth.toFloat(), this.mBorderWidth.toFloat())
                }

                this.mDrawableRadius = Math.min(this.mDrawableRect.height() / 2.0f, this.mDrawableRect.width() / 2.0f)
                if (this.mInnrCircleDiammeter > 1.0f) {
                    this.mInnrCircleDiammeter = 1.0f
                }

                this.mDrawableRadius *= this.mInnrCircleDiammeter
                this.applyColorFilter()
                this.updateShaderMatrix()
                this.invalidate()
            }
        }
    }

    private fun calculateBounds(): RectF {
        val availableWidth = this.width - this.paddingLeft - this.paddingRight
        val availableHeight = this.height - this.paddingTop - this.paddingBottom
        val sideLength = Math.min(availableWidth, availableHeight)
        val left = this.paddingLeft.toFloat() + (availableWidth - sideLength).toFloat() / 2.0f
        val top = this.paddingTop.toFloat() + (availableHeight - sideLength).toFloat() / 2.0f
        return RectF(left + this.borderWidth.toFloat(), top + this.borderWidth.toFloat(), left + sideLength.toFloat() - this.borderWidth.toFloat(), top + sideLength.toFloat() - this.borderWidth.toFloat())
    }

    private fun updateShaderMatrix() {
        var dx = 0.0f
        var dy = 0.0f
        this.mShaderMatrix.set(null)
        val scale: Float
        if (this.mBitmapWidth.toFloat() * this.mDrawableRect.height() > this.mDrawableRect.width() * this.mBitmapHeight.toFloat()) {
            scale = this.mDrawableRect.height() / this.mBitmapHeight.toFloat()
            dx = (this.mDrawableRect.width() - this.mBitmapWidth.toFloat() * scale) * 0.5f
        } else {
            scale = this.mDrawableRect.width() / this.mBitmapWidth.toFloat()
            dy = (this.mDrawableRect.height() - this.mBitmapHeight.toFloat() * scale) * 0.5f
        }

        this.mShaderMatrix.setScale(scale, scale)
        this.mShaderMatrix.postTranslate((dx + 0.5f).toInt().toFloat() + this.mDrawableRect.left, (dy + 0.5f).toInt().toFloat() + this.mDrawableRect.top)
        this.mBitmapShader!!.setLocalMatrix(this.mShaderMatrix)
    }

    companion object {
        private val SCALE_TYPE: ScaleType
        private val BITMAP_CONFIG: Config
        private val COLORDRAWABLE_DIMENSION = 2
        private var DEFAULT_INNTER_DAIMMETER_FRACTION: Float = 0.toFloat()
        private val DEFAULT_ANIMATION_TIME = 800
        private val DEFAULT_BORDER_WIDTH = 0
        private val DEFAULT_BORDER_COLOR = -16777216
        private val DEFAULT_FILL_COLOR = 0
        private val DEFAULT_PROGRESS_COLOR = -16776961
        private val DEFAULT_BORDER_OVERLAY = false

        init {
            SCALE_TYPE = ScaleType.CENTER_CROP
            BITMAP_CONFIG = Config.ARGB_8888
            DEFAULT_INNTER_DAIMMETER_FRACTION = 0.805f
        }
    }
}
