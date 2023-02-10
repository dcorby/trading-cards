package com.example.tradingcards

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout

class SquareView: RelativeLayout {

    lateinit var parent: RectangleView

    constructor(context: Context?) : super(context!!) {
        this.setOnTouchListener(onTouchListener)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //val paint = Paint()
        //paint.setColor(Color.parseColor("#0000FF"))
        //canvas.drawRect(20.toFloat(), 20.toFloat(), 20.toFloat(), 20.toFloat(), paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    fun show() {
        val params = LayoutParams(20, 20)
        var leftMargin = 0
        var topMargin = 0
        when(this.tag.toString()) {
            "left" -> {
                // Need to use params.height, not height, since not drawn yet
                leftMargin = -10
                topMargin = parent.params.height / 2 - 10
            }
            "top" -> {
                leftMargin = parent.params.width / 2 - 10
                topMargin = -10
            }
            "right" -> {
                leftMargin = parent.params.width - 10
                topMargin = parent.params.height / 2 - 10
            }
            "bottom" -> {
                leftMargin = parent.params.width / 2 - 10
                topMargin = parent.params.height - 10
            }
        }
        params.leftMargin = leftMargin
        params.topMargin = topMargin

        // right and bottomMargin necessary for right and bottom views to not get clipped. Why?
        params.rightMargin = 20
        params.bottomMargin = 20
        this.layoutParams = params
    }

    val onTouchListener = object : View.OnTouchListener {
        // https://stackoverflow.com/questions/7892853/how-to-use-correct-dragging-of-a-view-on-android/18806475#18806475
        var prevX = 0
        var prevY = 0
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            Log.v("TEST", "onTouch()")
            val params = v!!.layoutParams as LayoutParams
            when (event!!.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (v.tag in listOf("top", "bottom")) {
                        params.topMargin += event.rawY.toInt() - prevY
                        prevY = event.rawY.toInt()
                    }
                    if (v.tag in listOf("left", "right")) {
                        params.leftMargin += event.rawX.toInt() - prevX
                        prevX = event.rawX.toInt()
                    }
                    v.layoutParams = params
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (v.tag in listOf("top", "bottom")) {
                        params.topMargin += event.rawY.toInt() - prevY
                    }
                    if (v.tag in listOf("left", "right")) {
                        params.leftMargin += event.rawX.toInt() - prevX
                        v.layoutParams = params
                    }
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()
                    params.bottomMargin = -2 * v.height
                    params.rightMargin = -2 * v.width
                    v.layoutParams = params
                    return true
                }
            }
            return false
        }
    }
}