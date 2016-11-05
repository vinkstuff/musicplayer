package vinkovic.filip.musicplayer.ui.shared


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.support.annotation.IntDef
import android.support.annotation.IntRange
import android.support.annotation.NonNull
import android.support.v4.os.ParcelableCompat
import android.support.v4.os.ParcelableCompatCreatorCallbacks
import android.support.v4.view.AbsSavedState
import android.transition.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import vinkovic.filip.musicplayer.R

class MusicCoverView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ImageView(context, attrs, defStyleAttr), Animatable {

    companion object {

        const val SHAPE_RECTANGLE = 0
        const val SHAPE_CIRCLE = 1

        const val ALPHA_TRANSPARENT = 0
        const val ALPHA_OPAQUE = 255

        const val TRACK_SIZE = 10f
        const val TRACK_WIDTH = 1f
        val TRACK_COLOR = Color.parseColor("#56FFFFFF")

        const val FULL_ANGLE = 360f
        const val HALF_ANGLE = FULL_ANGLE / 2
        const val DURATION = 2500
        const val DURATION_PER_DEGREES = DURATION / FULL_ANGLE
    }

    private var mStartRotateAnimator: ValueAnimator
    private var mEndRotateAnimator: ValueAnimator
    private var mCircleToRectTransition: Transition
    private var mRectToCircleTransition: Transition

    private var mTrackSize: Float
    private var mTrackPaint: Paint
    private var mTrackAlpha: Int = 0

    private val mClipPath = Path()
    private val mRectPath = Path()
    private val mTrackPath = Path()

    private var mIsMorphing: Boolean = false
    private var mRadius = 0f

    private var mCallbacks: Callbacks? = null

    var shape: Int = 0
        set(@Shape shape) {
            if (shape != field) {
                field = shape
                setScaleType()
                if (!isInLayout && !isLayoutRequested) {
                    calculateRadius()
                    resetPaths()
                }
            }
        }

    var trackColor: Int
        get() = mTrackPaint.color
        set(@ColorInt trackColor) {
            if (trackColor != trackColor) {
                val alpha = if (shape == SHAPE_CIRCLE) ALPHA_OPAQUE else ALPHA_TRANSPARENT
                mTrackPaint.color = trackColor
                mTrackAlpha = Color.alpha(trackColor)
                mTrackPaint.alpha = alpha * mTrackAlpha / ALPHA_OPAQUE
                invalidate()
            }
        }

    init {

        // TODO: Canvas.clipPath works wrong when running with hardware acceleration on Android N
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }

        val density = resources.displayMetrics.density
        mTrackSize = TRACK_SIZE * density
        mTrackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTrackPaint.style = Paint.Style.STROKE
        mTrackPaint.strokeWidth = TRACK_WIDTH * density

        mEndRotateAnimator = ObjectAnimator.ofFloat<View>(this@MusicCoverView, View.ROTATION, 0f)
        mEndRotateAnimator.interpolator = LinearInterpolator()
        mEndRotateAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                rotation = 0f
                // isRunning method return true if it's called form here.
                // So we need call from post method to get the right returning.
                post {
                    if (mCallbacks != null) {
                        mCallbacks!!.onRotateEnd(this@MusicCoverView)
                    }
                }
            }
        })

        mStartRotateAnimator = ObjectAnimator.ofFloat<View>(this, View.ROTATION, 0f, FULL_ANGLE)
        mStartRotateAnimator.interpolator = LinearInterpolator()
        mStartRotateAnimator.repeatCount = Animation.INFINITE
        mStartRotateAnimator.duration = DURATION.toLong()
        mStartRotateAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val current = rotation
                val target = if (current > HALF_ANGLE) FULL_ANGLE else 0f // Choose the shortest distance to 0 rotation
                val diff = if (target > 0) FULL_ANGLE - current else current
                mEndRotateAnimator.setFloatValues(current, target)
                mEndRotateAnimator.duration = (DURATION_PER_DEGREES * diff).toInt().toLong()
                mEndRotateAnimator.start()
            }
        })



        mRectToCircleTransition = MorphTransition(SHAPE_RECTANGLE)
        mRectToCircleTransition.addTarget(this)
        mRectToCircleTransition.addListener(object : TransitionAdapter() {
            override fun onTransitionStart(transition: Transition) {
                mIsMorphing = true
            }

            override fun onTransitionEnd(transition: Transition) {
                mIsMorphing = false
                shape = SHAPE_CIRCLE
                if (mCallbacks != null) {
                    mCallbacks!!.onMorphEnd(this@MusicCoverView)
                }
            }
        })

        mCircleToRectTransition = MorphTransition(SHAPE_CIRCLE)
        mCircleToRectTransition.addTarget(this)
        mCircleToRectTransition.addListener(object : TransitionAdapter() {
            override fun onTransitionStart(transition: Transition) {
                mIsMorphing = true
            }

            override fun onTransitionEnd(transition: Transition) {
                mIsMorphing = false
                shape = SHAPE_RECTANGLE
                if (mCallbacks != null) {
                    mCallbacks!!.onMorphEnd(this@MusicCoverView)
                }
            }
        })

        val a = context.obtainStyledAttributes(attrs, R.styleable.MusicCoverView)
        @Shape val shape = a.getInt(R.styleable.MusicCoverView_shape, SHAPE_RECTANGLE)
        @ColorInt val trackColor = a.getColor(R.styleable.MusicCoverView_trackColor, TRACK_COLOR)
        a.recycle()

        this.shape = shape
        this.trackColor = trackColor
        setScaleType()
    }

    fun setCallbacks(callbacks: Callbacks) {
        mCallbacks = callbacks
    }


    internal var transitionRadius: Float
        get() = mRadius
        set(radius) {
            if (radius != mRadius) {
                mRadius = radius
                resetPaths()
                invalidate()
            }
        }

    internal var transitionAlpha: Int
        get() = mTrackPaint.alpha * ALPHA_OPAQUE / mTrackAlpha
        set(@IntRange(from = ALPHA_TRANSPARENT.toLong(), to = ALPHA_OPAQUE.toLong()) alpha) {
            if (alpha != transitionAlpha) {
                mTrackPaint.alpha = alpha * mTrackAlpha / ALPHA_OPAQUE
                invalidate()
            }
        }

    internal val minRadius: Float
        get() {
            val w = width
            val h = height
            return Math.min(w, h) / 2f
        }

    internal val maxRadius: Float
        get() {
            val w = width
            val h = height
            return Math.hypot((w / 2f).toDouble(), (h / 2f).toDouble()).toFloat()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateRadius()
        resetPaths()
    }

    private fun calculateRadius() {
        if (SHAPE_CIRCLE == shape) {
            mRadius = minRadius
        } else {
            mRadius = maxRadius
        }
    }

    private fun setScaleType() {
        if (SHAPE_CIRCLE == shape) {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        } else {
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun resetPaths() {

        val w = width
        val h = height
        val centerX = w / 2f
        val centerY = h / 2f

        mClipPath.reset()
        mClipPath.addCircle(centerX, centerY, mRadius, Path.Direction.CW)

        val trackRadius = Math.min(w, h)
        val trackCount = (trackRadius / mTrackSize).toInt()

        mTrackPath.reset()
        for (i in 3..trackCount - 1) {
            mTrackPath.addCircle(centerX, centerY, trackRadius * (i / trackCount.toFloat()), Path.Direction.CW)
        }

        mRectPath.reset()
        mRectPath.addRect(0f, 0f, w.toFloat(), h.toFloat(), Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(mClipPath)
        super.onDraw(canvas)
        canvas.drawPath(mTrackPath, mTrackPaint)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        // Don't need to consume the system window insets
        return insets
    }

    fun morph() {
        if (SHAPE_CIRCLE == shape) {
            morphToRect()
        } else {
            morphToCircle()
        }
    }

    private fun morphToCircle() {
        if (mIsMorphing) {
            return
        }
        TransitionManager.beginDelayedTransition(parent as ViewGroup, mRectToCircleTransition)
        scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    private fun morphToRect() {
        if (mIsMorphing) {
            return
        }
        TransitionManager.beginDelayedTransition(parent as ViewGroup, mCircleToRectTransition)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    override fun start() {
        if (SHAPE_RECTANGLE == shape) { // Only start rotate when shape is a circle
            return
        }
        if (!isRunning) {
            mStartRotateAnimator.start()
        }
    }

    override fun stop() {
        if (mStartRotateAnimator.isRunning) {
            mStartRotateAnimator.cancel()
        }
    }

    override fun isRunning(): Boolean {
        return mStartRotateAnimator.isRunning || mEndRotateAnimator.isRunning || mIsMorphing
    }

    @IntDef(SHAPE_CIRCLE.toLong(), SHAPE_RECTANGLE.toLong())
    @Retention(AnnotationRetention.SOURCE)
    annotation class Shape

    interface Callbacks {
        fun onMorphEnd(coverView: MusicCoverView)

        fun onRotateEnd(coverView: MusicCoverView)
    }

    private class MorphTransition constructor(shape: Int) : TransitionSet() {
        init {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(MusicCoverViewTransition(shape))
            addTransition(ChangeImageTransform())
            addTransition(ChangeTransform())
        }
    }

    /**
     * [SavedState] methods
     */

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.shape = shape
        ss.trackColor = trackColor
        ss.isRotating = mStartRotateAnimator.isRunning
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        shape = ss.shape
        trackColor = ss.trackColor
        if (ss.isRotating == true) {
            start()
        }
    }

    class SavedState : AbsSavedState {

        var shape: Int = 0
        var trackColor: Int = 0
        var isRotating: Boolean = false

        constructor(superState: Parcelable) : super(superState)

        private constructor(parcel: Parcel, loader: ClassLoader) : super(parcel, loader) {
            shape = parcel.readInt()
            trackColor = parcel.readInt()
            isRotating = parcel.readValue(Boolean::class.java.classLoader) as Boolean
        }

        override fun writeToParcel(@NonNull dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(shape)
            dest.writeInt(trackColor)
            dest.writeValue(isRotating)
        }

        override fun toString(): String {
            return MusicCoverView::class.java.simpleName + "." + SavedState::class.java.simpleName + "{" +
                    Integer.toHexString(System.identityHashCode(this)) +
                    " shape=" + shape + ", trackColor=" + trackColor + ", isRotating=" + isRotating + "}"
        }

        companion object {

            val CREATOR = ParcelableCompat.newCreator(object : ParcelableCompatCreatorCallbacks<SavedState> {
                override fun createFromParcel(parcel: Parcel, loader: ClassLoader): SavedState {
                    return SavedState(parcel, loader)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            })
        }
    }

    open class TransitionAdapter : Transition.TransitionListener {

        override fun onTransitionStart(transition: Transition) {
        }

        override fun onTransitionEnd(transition: Transition) {
        }

        override fun onTransitionCancel(transition: Transition) {
        }

        override fun onTransitionPause(transition: Transition) {
        }

        override fun onTransitionResume(transition: Transition) {
        }
    }
}