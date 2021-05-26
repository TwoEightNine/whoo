package global.msnthrp.whoo.ui.widgets

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import global.msnthrp.whoo.R
import global.msnthrp.whoo.databinding.ButtonSwipeBinding

class SwipeableButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val ANIMATION_DURATION = 200L
    }

    private enum class StateChangeDirection { CHECKED_UNCHECKED, UNCHECKED_CHECKED }

    var onSwipedListener: (() -> Unit)? = null
    var onSwipedOnListener: (() -> Unit)? = null
    var onSwipedOffListener: (() -> Unit)? = null

    /**
     * Current state
     * */
    var isChecked: Boolean = false
        set(isChecked) {
            field = isChecked
            rootView.post { updateState() }
        }

    /**
     * Enable click animation
     * */
    var isClickToSwipeEnable = true
        set(isClickToSwipeEnable) {
            field = isClickToSwipeEnable
            updateState()
        }

    /**
     * Parameter for setting swipe border for change state
     * from unchecked to checked.
     * Value must be from 0 to 1.
     * */
    var swipeProgressToFinish = 0.5
        set(swipeProgressToFinish) {
            if (swipeProgressToFinish >= 1 || swipeProgressToFinish <= 0) {
                throw Throwable("Illegal value argument. Available values from 0 to 1")
            }
            field = swipeProgressToFinish
            updateState()
        }

    /**
     * Parameter for setting swipe border for change state
     * from checked to unchecked.
     * Value must be from 0 to 1.
     * */
    var swipeProgressToStart = 0.5
        set(swipeProgressToStart) {
            if (swipeProgressToStart >= 1 || swipeProgressToStart <= 0) {
                throw Throwable("Illegal value argument. Available values from 0 to 1")
            }
            field = 1 - swipeProgressToStart
            updateState()
        }

    /**
     * Text that displaying when button is checked
     * */
    var checkedText: String = context.getString(R.string.checked_text)
        set(checkedText) {
            field = checkedText
            updateState()
        }

    /**
     * Text that displaying when button is unchecked
     * */
    var uncheckedText: String = context.getString(R.string.unchecked_text)
        set(uncheckedText) {
            field = uncheckedText
            updateState()
        }

    /**
     * Color of the text that displays when button is checked
     * */
    var checkedTextColor: Int = ContextCompat.getColor(context, android.R.color.white)
        set(checkedTextColor) {
            field = checkedTextColor
            updateState()
        }

    /**
     * Color of the text that displays when button is unchecked
     * */
    var uncheckedTextColor: Int = ContextCompat.getColor(context, android.R.color.white)
        set(uncheckedTextColor) {
            field = uncheckedTextColor
            updateState()
        }

    /**
     * Icon that displays when button is checked
     * */
    var checkedIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_stop)
        set(checkedIcon) {
            field = checkedIcon
            updateState()
        }

    /**
     * Icon that displays when button is unchecked
     * */
    var uncheckedIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_play)
        set(uncheckedIcon) {
            field = uncheckedIcon
            updateState()
        }

    /**
     * Background of swipeable button that displays when button is unchecked
     * */
    var uncheckedToggleBackground: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.shape_unchecked_toggle)
        set(uncheckedToggleBackground) {
            field = uncheckedToggleBackground
            updateState()
        }

    /**
     * Background of swipeable button that displays when button is checked
     * */
    var checkedToggleBackground: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.shape_checked_toggle)
        set(checkedToggleBackground) {
            field = checkedToggleBackground
            updateState()
        }

    /**
     * Background that displays when button is unchecked
     * */
    var uncheckedBackground: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.shape_scrolling_view_unchecked)
        set(uncheckedBackground) {
            field = uncheckedBackground
            updateState()
        }

    /**
     * Background that displays when button is checked
     * */
    var checkedBackground: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.shape_scrolling_view_checked)
        set(checkedBackground) {
            field = checkedBackground
            updateState()
        }

    /**
     * The size of displaying text
     * */
    var textSize: Float =
        context.resources.getDimensionPixelSize(R.dimen.default_text_size).toFloat()
        set(textSize) {
            field = textSize
            updateState()
        }

    /**
     * Setting is swipeable button enabled at this moment
     * */
    @get:JvmName("isEnabled_")
    @set:JvmName("isEnabled_")
    var isEnabled: Boolean = true
        set(isEnabled) {
            field = isEnabled

            updateEnableState()
        }

    /**
     * Duration of swipe animation.
     * Time in ms.
     * Value must be greater than 0.
     * */
    var animationDuration: Long = ANIMATION_DURATION
        set(animationDuration) {
            if (animationDuration <= 0) {
                throw Throwable("Illegal value argument. Value must be greater than 0.")
            }
            field = animationDuration
        }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return@OnTouchListener true
            MotionEvent.ACTION_MOVE -> {
                onButtonMove(event)
                this.parent.requestDisallowInterceptTouchEvent(true)
                return@OnTouchListener true
            }
            MotionEvent.ACTION_UP -> {
                onButtonMoved()
                return@OnTouchListener true
            }
        }
        v?.onTouchEvent(event) ?: true
    }

    private val onClickListener = OnClickListener {
        animateClick()
    }

    private var binding: ButtonSwipeBinding =
        ButtonSwipeBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            parseAttr(it)
        }

        updateState()
        updateEnableState()
    }

    /**
     * Setting current state of button with animation
     * @param isChecked set button state
     * */
    fun setCheckedAnimated(isChecked: Boolean) {
        if (isChecked) {
            animateToggleToEnd()
        } else {
            animateToggleToStart()
        }
    }

    /**
     * Setting initial toggle coordinate in checked state
     * */
    private fun updateEnableState() {
        if (this.isEnabled) {
            binding.slidingButtonIv.setOnTouchListener(onTouchListener)
            binding.slidingButtonIv.setOnClickListener(onClickListener)
            binding.buttonSwipeableView.setOnClickListener(onClickListener)
        } else {
            binding.slidingButtonIv.setOnClickListener(null)
            binding.slidingButtonIv.setOnTouchListener(null)
            binding.buttonSwipeableView.setOnClickListener(null)
        }
        binding.buttonSwipeableView.isEnabled = this.isEnabled
        binding.buttonSwipeableTv.isEnabled = this.isEnabled
        binding.slidingButtonIv.isEnabled = this.isEnabled
    }

    /**
     * Update button state and style.
     * Call when attribute change.
     * */
    private fun updateState() {
        if (this.isChecked) {
            setActivatedStyle()
            setToggleToEnd()
            setTextStartTextPadding()
        } else {
            setDeactivatedStyle()
            setToggleToStart()
            setTextEndTextPadding()
        }
    }

    /**
     * Setting initial toggle coordinate in unchecked state
     * */
    private fun setToggleToEnd() {
        binding.slidingButtonIv.x = (binding.buttonSwipeableView.width - binding.slidingButtonIv.width).toFloat()
    }

    /**
     * Setting initial toggle coordinate in checked state
     * */
    private fun setToggleToStart() {
        binding.slidingButtonIv.x = 0F
    }

    /**
     * Setting initial padding text in unchecked state
     * */
    private fun setTextStartTextPadding() {
        binding.buttonSwipeableTv.setPadding(0, 0, binding.slidingButtonIv.width, 0)
    }

    /**
     * Setting initial padding text in checked state
     * */
    private fun setTextEndTextPadding() {
        binding.buttonSwipeableTv.setPadding(binding.slidingButtonIv.width, 0, 0, 0)
    }

    /**
     * Animation when toggle returns to start position without changing state
     * */
    private fun returnToggleToStart() {
        val animatorSet = AnimatorSet()
        val positionAnimator = ValueAnimator.ofFloat(binding.slidingButtonIv.x, 0F)
        positionAnimator.duration = animationDuration
        positionAnimator.addUpdateListener {
            binding.slidingButtonIv.x = positionAnimator.animatedValue as Float
        }
        animatorSet.play(positionAnimator)
        animatorSet.start()
    }

    /**
     * Animation when toggle returns to end position without changing state
     * */
    private fun returnToggleToEnd() {
        val animatorSet = AnimatorSet()
        val positionAnimator = ValueAnimator.ofFloat(
            binding.slidingButtonIv.x,
            (binding.buttonSwipeableView.width - binding.slidingButtonIv.width).toFloat()
        )
        positionAnimator.duration = animationDuration
        positionAnimator.addUpdateListener {
            binding.slidingButtonIv.x = positionAnimator.animatedValue as Float
        }
        animatorSet.play(positionAnimator)
        animatorSet.start()
    }

    /**
     * Move the button to the start with the state changing (with animation)
     * */
    private fun animateToggleToStart() {
        val animatorSet = AnimatorSet()

        animateBackgroundChange(StateChangeDirection.CHECKED_UNCHECKED)
        animateToggleChange(StateChangeDirection.CHECKED_UNCHECKED)

        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), checkedTextColor, uncheckedTextColor)
        colorAnimation.duration = animationDuration
        colorAnimation.addUpdateListener { animator -> binding.buttonSwipeableTv.setTextColor(animator.animatedValue as Int) }

        val positionAnimator = ValueAnimator.ofFloat(binding.slidingButtonIv.x, 0F)
        positionAnimator.duration = animationDuration
        positionAnimator.addUpdateListener {
            binding.slidingButtonIv.x = positionAnimator.animatedValue as Float
        }

        val paddingAnimation = ValueAnimator.ofInt(0, binding.slidingButtonIv.width)
        paddingAnimation.duration = animationDuration
        paddingAnimation.addUpdateListener {
            binding.buttonSwipeableTv.setPadding(it.animatedValue as Int, 0, 0, 0)
        }

        val alphaAnimation = ValueAnimator.ofFloat(1F, 0F, 1F)
        alphaAnimation.duration = animationDuration
        alphaAnimation.addUpdateListener {
            if (it.animatedValue as Float <= 0.3) {
                if (binding.buttonSwipeableTv.text != uncheckedText) {
                    binding.buttonSwipeableTv.text = uncheckedText
                }
            }
            binding.buttonSwipeableTv.alpha = alphaAnimation.animatedValue as Float
        }

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setDeactivatedStyle()

                onSwipedOffListener?.invoke()
                onSwipedListener?.invoke()
                isChecked = false
            }
        })

        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.playTogether(positionAnimator, colorAnimation, alphaAnimation, paddingAnimation)
        animatorSet.start()
    }

    /**
     * Move the button to the end with the state changing (with animation)
     * */
    private fun animateToggleToEnd() {
        val animatorSet = AnimatorSet()

        animateBackgroundChange(StateChangeDirection.UNCHECKED_CHECKED)
        animateToggleChange(StateChangeDirection.UNCHECKED_CHECKED)

        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), uncheckedTextColor, checkedTextColor)
        colorAnimation.duration = animationDuration
        colorAnimation.addUpdateListener { animator -> binding.buttonSwipeableTv.setTextColor(animator.animatedValue as Int) }

        val positionAnimator = ValueAnimator.ofFloat(
            binding.slidingButtonIv.x,
            (binding.buttonSwipeableView.width - binding.slidingButtonIv.width).toFloat()
        )
        positionAnimator.duration = animationDuration
        positionAnimator.addUpdateListener {
            binding.slidingButtonIv.x = positionAnimator.animatedValue as Float
        }

        val alphaAnimation = ValueAnimator.ofFloat(1F, 0F, 1F)
        alphaAnimation.duration = animationDuration
        alphaAnimation.addUpdateListener {
            if (it.animatedValue as Float <= 0.3) {
                if (binding.buttonSwipeableTv.text != checkedText) {
                    binding.buttonSwipeableTv.text = checkedText
                }
            }
            binding.buttonSwipeableTv.alpha = alphaAnimation.animatedValue as Float
        }

        val paddingAnimation = ValueAnimator.ofInt(0, binding.slidingButtonIv.width)
        paddingAnimation.duration = animationDuration
        paddingAnimation.addUpdateListener {
            binding.buttonSwipeableTv.setPadding(0, 0, it.animatedValue as Int, 0)
        }

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setActivatedStyle()

                onSwipedOnListener?.invoke()
                onSwipedListener?.invoke()
                isChecked = true
            }
        })

        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.playTogether(positionAnimator, colorAnimation, alphaAnimation, paddingAnimation)
        animatorSet.start()
    }

    /**
     * Toggle click animation
     * */
    private fun animateClick() {
        if (this.isClickToSwipeEnable) {
            if (this.isChecked) {
                animateClickToActivate()
            } else {
                animateClickToDeactivate()
            }
        }
    }

    /**
     * An animation that is invoked when a user tries to click on an unchecked button
     * */
    private fun animateClickToActivate() {
        val animatorSet = AnimatorSet()

        val positionAnimator =
            ValueAnimator.ofFloat(
                (binding.buttonSwipeableView.width - binding.slidingButtonIv.width).toFloat(),
                ((binding.buttonSwipeableView.width - binding.slidingButtonIv.width) - (binding.slidingButtonIv.width / 2)).toFloat(),
                (binding.buttonSwipeableView.width - binding.slidingButtonIv.width).toFloat()
            )
        positionAnimator.addUpdateListener {
            binding.slidingButtonIv.x = positionAnimator.animatedValue as Float
        }
        animatorSet.play(positionAnimator)
        animatorSet.start()
    }

    /**
     * An animation that is invoked when a user tries to click on an checked button
     * */
    private fun animateClickToDeactivate() {
        val animatorSet = AnimatorSet()

        val positionAnimator =
            ValueAnimator.ofFloat(
                0F,
                (binding.slidingButtonIv.width / 2).toFloat(),
                0F
            )
        positionAnimator.addUpdateListener {
            binding.slidingButtonIv.x = positionAnimator.animatedValue as Float
        }
        animatorSet.play(positionAnimator)
        animatorSet.start()
    }

    /**
     * Animation change button background.
     * @param direction set animation direction
     * */
    private fun animateBackgroundChange(direction: StateChangeDirection) {
        val backgrounds = arrayOfNulls<Drawable>(2)

        if (direction == StateChangeDirection.UNCHECKED_CHECKED) {
            backgrounds[0] = uncheckedBackground
            backgrounds[1] = checkedBackground
        } else {
            backgrounds[0] = checkedBackground
            backgrounds[1] = uncheckedBackground
        }

        val backgroundTransition = TransitionDrawable(backgrounds)
        binding.buttonSwipeableView.background = backgroundTransition
        backgroundTransition.startTransition(animationDuration.toInt())
    }

    /**
     * Animation change toggle background.
     * @param direction set animation direction
     * */
    private fun animateToggleChange(direction: StateChangeDirection) {
        val backgrounds = arrayOfNulls<Drawable>(2)

        if (direction == StateChangeDirection.UNCHECKED_CHECKED) {
            backgrounds[0] = uncheckedToggleBackground
            backgrounds[1] = checkedToggleBackground
        } else {
            backgrounds[0] = checkedToggleBackground
            backgrounds[1] = uncheckedToggleBackground
        }

        val backgroundTransition = TransitionDrawable(backgrounds)
        binding.slidingButtonIv.background = backgroundTransition
        backgroundTransition.startTransition(animationDuration.toInt())
    }

    /**
     * Animation change toggle background.
     * @param event parameter with a new coordinate
     * */
    private fun onButtonMove(event: MotionEvent) {
        val newCoordinates = binding.slidingButtonIv.x + event.x

        if (binding.slidingButtonIv.x >= 0
            && newCoordinates + binding.slidingButtonIv.width / 2 < width
        ) {
            if (binding.slidingButtonIv.x + binding.slidingButtonIv.width / 2 < newCoordinates
                || newCoordinates - binding.slidingButtonIv.width / 2 > binding.buttonSwipeableView.x
            ) {
                binding.slidingButtonIv.x = newCoordinates - binding.slidingButtonIv.width / 2
            }
        }
    }

    private fun onButtonMoved() {
        if (this.isChecked) {
            if (binding.slidingButtonIv.x < binding.buttonSwipeableView.width * swipeProgressToStart) {
                animateToggleToStart()
            } else {
                returnToggleToEnd()
            }
        } else {
            if (binding.slidingButtonIv.x > binding.buttonSwipeableView.width * swipeProgressToFinish) {
                animateToggleToEnd()
            } else {
                returnToggleToStart()
            }
        }
    }

    /**
     * Parse attributes from xml.
     * @param attrs passed attributes from XML file
     * */
    private fun parseAttr(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeableButton)

        isChecked = typedArray.getBoolean(R.styleable.SwipeableButton_isChecked, false)
        isClickToSwipeEnable =
                typedArray.getBoolean(R.styleable.SwipeableButton_isClickToSwipeEnable, true)
        swipeProgressToFinish = typedArray.getFloat(
            R.styleable.SwipeableButton_thresholdEnd,
            swipeProgressToFinish.toFloat()
        ).toDouble()
        swipeProgressToStart = 1 - typedArray.getFloat(
            R.styleable.SwipeableButton_thresholdStart,
            swipeProgressToStart.toFloat()
        ).toDouble()

        checkedText = typedArray.getString(R.styleable.SwipeableButton_textChecked)
                ?: context.getString(
            typedArray.getResourceId(
                R.styleable.SwipeableButton_textChecked,
                R.string.checked_text
            )
        )

        uncheckedText = typedArray.getString(R.styleable.SwipeableButton_textUnchecked)
                ?: context.getString(
            typedArray.getResourceId(
                R.styleable.SwipeableButton_textUnchecked,
                R.string.unchecked_text
            )
        )

        checkedTextColor =
                if (typedArray.getInt(R.styleable.SwipeableButton_textColorChecked, 0) != 0) {
                    typedArray.getInt(R.styleable.SwipeableButton_textColorChecked, 0)
                } else {
                    ContextCompat.getColor(
                        context,
                        typedArray.getResourceId(
                            R.styleable.SwipeableButton_textColorChecked,
                            android.R.color.white
                        )
                    )
                }

        uncheckedTextColor =
                if (typedArray.getInt(
                        R.styleable.SwipeableButton_textColorUnChecked,
                        0
                    ) != 0
                ) {
                    typedArray.getInt(R.styleable.SwipeableButton_textColorUnChecked, 0)
                } else {
                    ContextCompat.getColor(
                        context,
                        typedArray.getResourceId(
                            R.styleable.SwipeableButton_textColorUnChecked,
                            android.R.color.white
                        )
                    )
                }

        checkedIcon = typedArray.getDrawable(R.styleable.SwipeableButton_checkedIcon)
                ?: ContextCompat.getDrawable(
            context,
            typedArray.getResourceId(
                R.styleable.SwipeableButton_checkedIcon,
                R.drawable.ic_stop
            )
        )
        uncheckedIcon = typedArray.getDrawable(R.styleable.SwipeableButton_uncheckedIcon)
                ?: ContextCompat.getDrawable(
            context,
            typedArray.getResourceId(
                R.styleable.SwipeableButton_uncheckedIcon,
                R.drawable.ic_play
            )
        )

        uncheckedToggleBackground = ContextCompat.getDrawable(
            context,
            typedArray.getResourceId(
                R.styleable.SwipeableButton_uncheckedToggleBackground,
                R.drawable.shape_unchecked_toggle
            )
        )

        checkedToggleBackground = ContextCompat.getDrawable(
            context,
            typedArray.getResourceId(
                R.styleable.SwipeableButton_checkedToggleBackground,
                R.drawable.shape_checked_toggle
            )
        )

        checkedBackground = ContextCompat.getDrawable(
            context,
            typedArray.getResourceId(
                R.styleable.SwipeableButton_checkedBackground,
                R.drawable.shape_scrolling_view_checked
            )
        )
        uncheckedBackground = ContextCompat.getDrawable(
            context,
            typedArray.getResourceId(
                R.styleable.SwipeableButton_uncheckedBackground,
                R.drawable.shape_scrolling_view_unchecked
            )
        )

        textSize = if (typedArray.getDimensionPixelSize(
                R.styleable.SwipeableButton_textSize,
                0
            ) != 0
        ) {
            typedArray.getDimensionPixelSize(R.styleable.SwipeableButton_textSize, 0)
                .toFloat()
        } else {
            context.resources.getDimensionPixelSize(R.dimen.default_text_size).toFloat()
        }

        animationDuration = typedArray.getFloat(
            R.styleable.SwipeableButton_durationAnimation,
            animationDuration.toFloat()
        ).toLong()

        typedArray.recycle()
    }

    private fun setActivatedStyle() {
        binding.buttonSwipeableView.background = checkedBackground
        binding.slidingButtonIv.background = checkedToggleBackground
        binding.slidingButtonIv.setImageDrawable(checkedIcon)
        if (binding.buttonSwipeableTv.text != checkedText) {
            binding.buttonSwipeableTv.text = checkedText
        }
        binding.buttonSwipeableTv.textSize = textSize
        binding.buttonSwipeableTv.setTextColor(checkedTextColor)
    }

    private fun setDeactivatedStyle() {
        binding.buttonSwipeableView.background = uncheckedBackground
        binding.slidingButtonIv.background = uncheckedToggleBackground
        binding.slidingButtonIv.setImageDrawable(uncheckedIcon)
        if (binding.buttonSwipeableTv.text != uncheckedText) {
            binding.buttonSwipeableTv.text = uncheckedText
        }
        binding.buttonSwipeableTv.textSize = textSize
        binding.buttonSwipeableTv.setTextColor(uncheckedTextColor)
    }
}