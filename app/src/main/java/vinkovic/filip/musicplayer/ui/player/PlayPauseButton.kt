package vinkovic.filip.musicplayer.ui.player

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import vinkovic.filip.musicplayer.R

class PlayPauseButton : ImageView {

    enum class State {
        PLAY, PAUSE
    }

    private var playToPauseDrawable: Drawable? = null

    private var pauseToPlayDrawable: Drawable? = null

    private var state: State = State.PLAY

    private var bounds: Rect? = null

    private var backgroundPaint: Paint = Paint()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        playToPauseDrawable = context.getDrawable(R.drawable.play_to_pause)
        pauseToPlayDrawable = context.getDrawable(R.drawable.pause_to_play)

        backgroundPaint.color = Color.WHITE

        bounds = Rect()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.getClipBounds(bounds)
        canvas.drawCircle(bounds!!.centerX().toFloat(), bounds!!.centerY().toFloat(), bounds!!.height() / 2f, backgroundPaint)
        super.onDraw(canvas)
    }

    fun setState(newState: State) {
        state = newState
        val drawable: AnimatedVectorDrawable = (if (state == State.PLAY) playToPauseDrawable else pauseToPlayDrawable)
                as AnimatedVectorDrawable
        setImageDrawable(drawable)
        drawable.start()
    }
}