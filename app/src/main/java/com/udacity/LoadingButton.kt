package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var textHeight = 0f
    private var textWidth = 0f
    private var textOffset = 0f
    private val textSize: Float = resources.getDimension(R.dimen.standard_text_size)
    private val circle = RectF(0f, 0f, textSize, textSize)

    private var circleDiameter = 0f
    private var circleMargin = 0f

    private var horizontalProgress = RectF().apply {
        left = 0f
        top = 0f
        right = 0f
        bottom = 0f
    }

    private var downloadProgress = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var circleProgress = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var circleColor = 0
    private val circlePaint = Paint()

    // Text
    private var buttonText: String = resources.getString(R.string.button_download)
    private val buttonTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 20f * resources.displayMetrics.scaledDensity
    }

    // Original color for background
    private var buttonOriginalColor = 0
    private val buttonOriginalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    // Downloading progress color
    private var buttonDownloadingColor = 0
    private val buttonDownloadingPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Complete) { _, _, newState ->
        when (newState) {
            ButtonState.Downloading -> {
                valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                    duration = 2500
                    repeatMode = ValueAnimator.RESTART
                    repeatCount = ValueAnimator.INFINITE

                    addUpdateListener {
                        downloadProgress = animatedValue as Float * measuredWidth.toFloat()
                        circleProgress = (widthSize.toFloat() / 365) * downloadProgress
                    }
                    start()
                }
            }
            ButtonState.Complete -> {
                valueAnimator.end()
            }
        }
    }

    // First time setup for the view
    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonOriginalColor = getColor(R.styleable.LoadingButton_buttonOriginalColor, 0)
            buttonDownloadingColor = getColor(R.styleable.LoadingButton_buttonDownloadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }

        buttonOriginalPaint.color = buttonOriginalColor
        buttonDownloadingPaint.color = buttonDownloadingColor
        circlePaint.color = circleColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(buttonOriginalColor)
        canvas.drawText(
            buttonText,
            width.toFloat() / 2,
            ((height / 2) - ((buttonTextPaint.descent() + buttonTextPaint.ascent()) / 2)),
            buttonTextPaint
        )

        if (buttonState == ButtonState.Downloading) {
            canvas.drawRect(0f, 0f, downloadProgress, heightSize.toFloat(), buttonDownloadingPaint)

            buttonText = resources.getString(R.string.button_downloading)
            canvas.drawText(
                buttonText,
                width.toFloat() / 2,
                ((height / 2) - ((buttonTextPaint.descent() + buttonTextPaint.ascent()) / 2)),
                buttonTextPaint
            )

            textWidth = buttonTextPaint.measureText(buttonText)

            canvas.save()
            canvas.translate(
                widthSize / 2 + textWidth / 2 + textSize / 2,
                heightSize / 2 - textSize / 2
            )
            canvas.drawArc(circle, 0F, circleProgress * 0.365f, true, circlePaint)
            canvas.restore()
        }
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

        textHeight = (buttonTextPaint.descent().toDouble() - buttonTextPaint.ascent()).toFloat()
        textWidth = buttonTextPaint.measureText(buttonText)
        textOffset = (textHeight / 2) - buttonTextPaint.descent()

        circleDiameter = heightSize * 0.5f
        circleMargin = heightSize * 0.2f

        horizontalProgress.right = widthSize.toFloat()
        horizontalProgress.bottom = heightSize.toFloat()

        setMeasuredDimension(w, h)
    }

    private fun defineButtonState(buttonState: ButtonState) {
        this.buttonState = buttonState
    }

    private fun stopAnimation() {
        defineButtonState(ButtonState.Complete)
        valueAnimator.cancel()
        downloadProgress = 0f
        circleProgress = 0f
        horizontalProgress.apply {
            left = 0f
            top = 0f
            right = 0f
            bottom = 0f
        }
    }

    fun downloadComplete() {
        stopAnimation()
        defineButtonState(ButtonState.Complete)
    }

    fun startDownload() {
        defineButtonState(ButtonState.Downloading)
    }
}