/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vinkovic.filip.musicplayer.ui.shared


import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.Property
import android.view.ViewGroup
import vinkovic.filip.musicplayer.R
import java.util.*

class MusicCoverViewTransition : Transition {

    companion object {

        private val PROPNAME_RADIUS = MusicCoverViewTransition::class.java.name + ":radius"
        private val PROPNAME_ALPHA = MusicCoverViewTransition::class.java.name + ":alpha"
        private val sTransitionProperties = arrayOf(PROPNAME_RADIUS, PROPNAME_ALPHA)

        private val RADIUS_PROPERTY = object : Property<MusicCoverView, Float>(Float::class.java, "radius") {
            override operator fun set(view: MusicCoverView, radius: Float) {
                view.transitionRadius = radius
            }

            override operator fun get(view: MusicCoverView): Float {
                return view.transitionRadius
            }
        }

        private val ALPHA_PROPERTY = object : Property<MusicCoverView, Int>(Int::class.java, "alpha") {
            override operator fun set(view: MusicCoverView, alpha: Int) {
                view.transitionAlpha = alpha
            }

            override operator fun get(view: MusicCoverView): Int {
                return view.transitionAlpha
            }
        }
    }

    private val mStartShape: Int

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MusicCoverView)
        val shape = a.getInt(R.styleable.MusicCoverView_shape, MusicCoverView.SHAPE_RECTANGLE)
        a.recycle()
        mStartShape = shape
    }

    constructor(shape: Int) {
        mStartShape = shape
    }

    override fun getTransitionProperties(): Array<String> {
        return sTransitionProperties
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        // Add fake value to force calling of createAnimator method
        captureValues(transitionValues, "start")
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        // Add fake value to force calling of createAnimator method
        captureValues(transitionValues, "end")
    }

    private fun captureValues(transitionValues: TransitionValues, value: Any) {
        if (transitionValues.view is MusicCoverView) {
            transitionValues.values.put(PROPNAME_RADIUS, value)
            transitionValues.values.put(PROPNAME_ALPHA, value)
        }
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues, endValues: TransitionValues?): Animator? {

        if (endValues == null || endValues.view !is MusicCoverView) {
            return null
        }

        val coverView = endValues.view as MusicCoverView
        val minRadius = coverView.minRadius
        val maxRadius = coverView.maxRadius

        val startRadius: Float
        val endRadius: Float
        val startTrackAlpha: Int
        val endTrackAlpha: Int

        if (mStartShape == MusicCoverView.SHAPE_RECTANGLE) {
            startRadius = maxRadius
            endRadius = minRadius
            startTrackAlpha = MusicCoverView.ALPHA_TRANSPARENT
            endTrackAlpha = MusicCoverView.ALPHA_OPAQUE
        } else {
            startRadius = minRadius
            endRadius = maxRadius
            startTrackAlpha = MusicCoverView.ALPHA_OPAQUE
            endTrackAlpha = MusicCoverView.ALPHA_TRANSPARENT
        }

        val animatorList = ArrayList<Animator>()

        coverView.transitionRadius = startRadius
        animatorList.add(ObjectAnimator.ofFloat(coverView, RADIUS_PROPERTY, startRadius, endRadius))

        coverView.transitionAlpha = startTrackAlpha
        animatorList.add(ObjectAnimator.ofInt(coverView, ALPHA_PROPERTY, startTrackAlpha, endTrackAlpha))

        val animator = AnimatorSet()
        animator.playTogether(animatorList)
        return animator
    }
}
