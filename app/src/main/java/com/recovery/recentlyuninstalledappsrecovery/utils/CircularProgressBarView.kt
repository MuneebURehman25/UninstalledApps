package com.recovery.recentlyuninstalledappsrecovery.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircularProgressBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val progressColors = intArrayOf(

        0xFF68EE76.toInt(),// Green
        0xFF4FBAF0.toInt(),// Blue
        0xFFF29FFF.toInt() // Purple

    )
    private val progressBarWidth = 50f
    private val gapWidth = 5f

    private val paint = Paint()

    private var maxProgress = 73f
    private var progressData = floatArrayOf(20f, 35f, 15f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - progressBarWidth / 2

        var startAngle = -90f // Start from the top

        for (i in 0 until progressData.size) {
            val sweepAngle = (360f * progressData[i] / maxProgress)
            paint.color = progressColors[i]
            paint.strokeWidth = progressBarWidth
            paint.style = Paint.Style.STROKE

            // Draw the progress
            canvas?.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                false,
                paint
            )

            startAngle += sweepAngle + gapWidth
        }
    }

    fun setMaxProgress(maxProgress: Float) {
        this.maxProgress = maxProgress
        invalidate()
    }

    fun setProgressData(progressData: FloatArray) {
        this.progressData = progressData
        invalidate()
    }
}
