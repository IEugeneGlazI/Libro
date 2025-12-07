package com.example.libro.ui.stats

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.libro.R

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        strokeCap = Paint.Cap.ROUND
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        color = ContextCompat.getColor(context, android.R.color.darker_gray)
    }

    private val rectF = RectF()
    
    var readPercent: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }
    
    var readingPercent: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }
    
    var plannedPercent: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (minOf(width, height) / 2f) - 40f
        
        rectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        
        // Рисуем фоновый круг
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        
        // Рисуем сегменты
        var startAngle = -90f
        
        // Прочитано (зеленый)
        if (readPercent > 0) {
            paint.color = ContextCompat.getColor(context, R.color.green_500)
            val sweepAngle = 360f * readPercent / 100f
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }
        
        // Читаю (фиолетовый)
        if (readingPercent > 0) {
            paint.color = ContextCompat.getColor(context, R.color.purple_500)
            val sweepAngle = 360f * readingPercent / 100f
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }
        
        // В планах (серый)
        if (plannedPercent > 0) {
            paint.color = ContextCompat.getColor(context, android.R.color.darker_gray)
            val sweepAngle = 360f * plannedPercent / 100f
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
        }
    }
}

