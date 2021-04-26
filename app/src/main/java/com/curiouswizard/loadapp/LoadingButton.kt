package com.curiouswizard.loadapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

/**
 * Custom download button view, inherits from View.
 */
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var bounds = RectF()
    private var valueAnimator = ValueAnimator()

    // Define the default color for background
    private var defaultColor = 0
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    // Variables for loading color
    private var loadingColor = 0
    private val loadingPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    // Define text
    private var buttonText: String = resources.getString(R.string.button_name)
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 24F * resources.displayMetrics.scaledDensity
    }
    private var textHeight = 0.0F
    private var textOffset = 0.0F

    // Variables for the animation from 0F to 1F
    private var loadingProgress = 0F
        set(value) {
            field = value
            invalidate()
        }
    private var arcProgress = 0F
        set(value) {
            field = value
            invalidate()
        }

    // Variables for arc
    private var arcDiameter: Float = 0.0F
    private var arcMargin: Float = 0.0F
    private var arcColor = 0
    private val arcPaint = Paint()

    // Processing different states of the button
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new){
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F).apply {
                    duration = 1000L

                    addUpdateListener {
                        loadingProgress = animatedValue as Float * measuredWidth.toFloat()
                        arcProgress = animatedValue as Float * 360F
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.end()
            }
            ButtonState.Clicked -> {/* Does nothing */}
        }
    }

    // First time setup for the view
    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_progressColor, 0)
            arcColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }

        buttonPaint.color = defaultColor
        loadingPaint.color = loadingColor
        arcPaint.color = arcColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background of button
        canvas.drawRect(0F,0F, widthSize.toFloat(), heightSize.toFloat(), buttonPaint)

        if(buttonState == ButtonState.Loading){
            // Draw the loading rectangle on top of the background
            canvas.drawRect(0F, 0F, loadingProgress, heightSize.toFloat(), loadingPaint)

            // Draw oval within the given angles
            canvas.drawArc(
                widthSize - arcDiameter - arcMargin,
                arcMargin,
                widthSize - arcMargin,
                arcMargin + arcDiameter,
                0F,
                arcProgress,
                true,
                arcPaint
            )
        }

        // Draw text
        canvas.drawText(buttonText, bounds.centerX(), bounds.centerY() + textOffset, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h

        textHeight = (textPaint.descent().toDouble() - textPaint.ascent()).toFloat()
        textOffset = (textHeight / 2) - textPaint.descent()

        arcDiameter = heightSize * 0.6F
        arcMargin = heightSize * 0.1F

        bounds.right = widthSize.toFloat()
        bounds.bottom = heightSize.toFloat()

        setMeasuredDimension(w, h)
    }

    // Changing state of LoadingButton
    fun setState(state: ButtonState){
        this.buttonState = state
    }
}