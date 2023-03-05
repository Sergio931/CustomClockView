package com.sergio931.customclockview

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import android.os.Parcelable

class ClockView constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    companion object {
        private const val STATE_HOUR = "hour"
        private const val STATE_MINUTE = "minute"
        private const val STATE_SECOND = "second"

    }

    //Параметры для стрелок
    private val HOUR_HAND_COLOR = Color.BLACK
    private val MINUTE_HAND_COLOR = Color.BLACK
    private val SECOND_HAND_COLOR = Color.BLACK
    private val HOUR_HAND_LENGTH = 70f
    private val MINUTE_HAND_LENGTH = 100f
    private val SECOND_HAND_LENGTH = 130f
    private val HOUR_HAND_THICK = 20f
    private val MINUTE_HAND_THICK = 15f
    private val SECOND_HAND_THICK = 10f

    //Параметры для часов
    private val BORDER_WIDTH = 20f
    private val BORDER_COLOR = Color.BLACK
    private val BACKGROUND_COLOR = Color.WHITE
    private val DOT_RADIUS = 5f
    private val DOT_COLOR = Color.BLACK
    private val CLOCK_RADIUS = 200f

    private var centerX = 0f
    private var centerY = 0f

    //Параметры для шкалы часов
    private val SCALE_COLOR = Color.BLACK
    private val SCALE_WIDTH = 3f
    private val SCALE_HEIGHT = 30f
    private val SCALE_PADDING = 50f

    private val calendar = Calendar.getInstance()

    private val hourHandPaint = Paint().apply {
        color = HOUR_HAND_COLOR
        strokeWidth = HOUR_HAND_THICK
        isAntiAlias = true
    }
    private val minuteHandPaint = Paint().apply {
        color = MINUTE_HAND_COLOR
        strokeWidth = MINUTE_HAND_THICK
        isAntiAlias = true
    }
    private val secondHandPaint = Paint().apply {
        color = SECOND_HAND_COLOR
        strokeWidth = SECOND_HAND_THICK
        isAntiAlias = true
    }
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = BORDER_WIDTH
        color = BORDER_COLOR
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = BACKGROUND_COLOR
        isAntiAlias = true
    }

    private val dotPaint = Paint().apply {
        style = Paint.Style.FILL
        color = DOT_COLOR
        isAntiAlias = true
    }


    private val hourScalePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = SCALE_COLOR
        style = Paint.Style.STROKE
        strokeWidth = SCALE_WIDTH
        textSize = SCALE_HEIGHT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
        centerX = size / 2f
        centerY = size / 2f
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas?.let {
            // Draw background
            it.drawCircle(centerX, centerY, CLOCK_RADIUS, backgroundPaint)

            drawHours(it)

            // Draw border
            it.drawCircle(centerX, centerY, CLOCK_RADIUS - BORDER_WIDTH / 2, borderPaint)

            // Draw dots
            for (i in 0..59) {
                val dotAngle = i * 360 / 60.toFloat()
                val dotX =
                    centerX + sin(Math.toRadians(dotAngle.toDouble())).toFloat() * (CLOCK_RADIUS - BORDER_WIDTH - DOT_RADIUS)
                val dotY =
                    centerY - cos(Math.toRadians(dotAngle.toDouble())).toFloat() * (CLOCK_RADIUS - BORDER_WIDTH - DOT_RADIUS)
                it.drawCircle(dotX, dotY, DOT_RADIUS, dotPaint)
            }

            // Draw hour hand
            drawHands(it)

            postInvalidateDelayed(1000)
        }
    }

    private fun drawHands(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hour hand
        val hourAngle = (hour + minute / 60f) * 30f - 90f
        val hourX = centerX + Math.cos(Math.toRadians(hourAngle.toDouble())) * HOUR_HAND_LENGTH
        val hourY = centerY + Math.sin(Math.toRadians(hourAngle.toDouble())) * HOUR_HAND_LENGTH
        canvas.drawLine(centerX, centerY, hourX.toFloat(), hourY.toFloat(), hourHandPaint)

        // Minute hand
        val minuteAngle = minute * 6f - 90f
        val minuteX =
            centerX + Math.cos(Math.toRadians(minuteAngle.toDouble())) * MINUTE_HAND_LENGTH
        val minuteY =
            centerY + Math.sin(Math.toRadians(minuteAngle.toDouble())) * MINUTE_HAND_LENGTH
        canvas.drawLine(centerX, centerY, minuteX.toFloat(), minuteY.toFloat(), minuteHandPaint)

        // Second hand
        val secondAngle = second * 6f - 90f
        val secondX =
            centerX + Math.cos(Math.toRadians(secondAngle.toDouble())) * SECOND_HAND_LENGTH
        val secondY =
            centerY + Math.sin(Math.toRadians(secondAngle.toDouble())) * SECOND_HAND_LENGTH
        canvas.drawLine(centerX, centerY, secondX.toFloat(), secondY.toFloat(), secondHandPaint)
    }

    private fun drawHours(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        // Draw scales
        for (i in 1..12) {
            val angle = i * 360 / 12.toFloat() - 90

            val startX =
                centerX + Math.cos(Math.toRadians(angle.toDouble())) * (CLOCK_RADIUS - SCALE_PADDING)
            val startY =
                centerY + Math.sin(Math.toRadians(angle.toDouble())) * (CLOCK_RADIUS - SCALE_PADDING)

            canvas.drawText(
                i.toString(),
                startX.toFloat() - 10f,
                startY.toFloat() + 10f,
                hourScalePaint
            )
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        super.onSaveInstanceState()

        return Bundle().apply {
            putInt(STATE_HOUR, calendar.get(Calendar.HOUR))
            putInt(STATE_MINUTE, calendar.get(Calendar.MINUTE))
            putInt(STATE_SECOND, calendar.get(Calendar.SECOND))
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        var restoredState = state

        if (state is Bundle) {
            calendar.set(Calendar.HOUR, state.getInt(STATE_HOUR))
            calendar.set(Calendar.MINUTE, state.getInt(STATE_MINUTE))
            calendar.set(Calendar.SECOND, state.getInt(STATE_SECOND))
        }
        super.onRestoreInstanceState(restoredState)
    }

}