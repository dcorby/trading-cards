package com.example.tradingcards

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class CircleView(context: Context?) : View(context) {
    //var paint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //paint.style = Paint.Style.FILL
        //paint.color = Color.parseColor("#0000FF")
        //canvas.drawPaint(paint)
        val paint = Paint()
        paint.setColor(Color.parseColor("#0000FF"))
        canvas.drawCircle(10.toFloat(), 10.toFloat(), 10.toFloat(), paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}