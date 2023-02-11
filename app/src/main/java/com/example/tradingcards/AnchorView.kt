package com.example.tradingcards

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginTop

class AnchorView: RelativeLayout {

    lateinit var rectangleView: RectangleView

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
                leftMargin = rectangleView.params.leftMargin - 10
                topMargin = rectangleView.params.topMargin + rectangleView.params.height / 2 - 10
            }
            "top" -> {
                leftMargin = rectangleView.params.leftMargin + rectangleView.params.width / 2 - 10
                topMargin = rectangleView.params.topMargin - 10
            }
            "right" -> {
                leftMargin = rectangleView.params.leftMargin + rectangleView.params.width - 10
                topMargin = rectangleView.params.topMargin + rectangleView.params.height / 2 - 10
            }
            "bottom" -> {
                leftMargin = rectangleView.params.leftMargin + rectangleView.params.width / 2 - 10
                topMargin = rectangleView.params.topMargin + rectangleView.params.height - 10
            }
        }
        params.leftMargin = leftMargin
        params.topMargin = topMargin
        this.layoutParams = params
    }

    fun hide() {

    }

    val onTouchListener = object : View.OnTouchListener {
        // https://stackoverflow.com/questions/7892853/how-to-use-correct-dragging-of-a-view-on-android/18806475#18806475
        var prevX = 0
        var prevY = 0
        override fun onTouch(anchorView: View?, event: MotionEvent?): Boolean {
            if (anchorView == null || event == null) { return false }
            val direction = anchorView.tag
            val anchorParams = anchorView.layoutParams as LayoutParams
            val rectangleView = this@AnchorView.rectangleView
            val rectangleParams = rectangleView.layoutParams as LayoutParams

            TODO: RE-POSITION THE OTHER AXIS ANCHORS
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (direction == "top" || direction == "bottom") {
                        // Get the diff
                        val diff = event.rawY.toInt() - prevY
                        // Move the anchor
                        anchorParams.topMargin += diff
                        prevY = event.rawY.toInt()
                        // Move the rect
                        if (direction == "top") {
                            rectangleParams.topMargin += diff
                            rectangleParams.height += diff*-1
                        }
                        if (direction == "bottom") {
                            rectangleParams.height += diff
                        }
                    }
                    if (direction == "left" || direction == "right") {
                        // Get the diff
                        val diff = event.rawX.toInt() - prevX
                        // Move the anchor
                        anchorParams.leftMargin += diff
                        prevX = event.rawX.toInt()
                        // Move the rect
                        if (direction == "left") {
                            rectangleParams.leftMargin += diff
                            rectangleParams.width += diff*-1
                        }
                        if (direction == "right") {
                            rectangleParams.width += diff
                        }
                    }
                    anchorView.layoutParams = anchorParams
                    rectangleView.layoutParams = rectangleParams
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (direction == "top" || direction == "bottom") {
                        anchorParams.topMargin += event.rawY.toInt() - prevY
                    }
                    if (direction == "left" || direction == "right") {
                        anchorParams.leftMargin += event.rawX.toInt() - prevX
                        anchorView.layoutParams = anchorParams
                    }
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    // Get data for anchor
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()

                    // little confused about bottom/right margins and the values??
                    anchorParams.bottomMargin = -2 * anchorView.height
                    anchorParams.rightMargin = -2 * anchorView.width
                    anchorView.layoutParams = anchorParams
                    rectangleParams.bottomMargin = -2 * rectangleParams.height
                    rectangleParams.rightMargin = -2 * rectangleParams.width
                    rectangleView.layoutParams = rectangleParams
                    return true
                }
            }
            return false
        }
    }
}