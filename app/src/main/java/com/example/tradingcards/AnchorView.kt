package com.example.tradingcards

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

class AnchorView: RelativeLayout {

    lateinit var rectangleView: RectangleView
    // Setting this.layoutParams fails per smartcast issue. Get a reference
    lateinit var params: LayoutParams

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
        params = LayoutParams(20, 20)
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
        this.visibility = View.INVISIBLE
    }

    val onTouchListener = object : View.OnTouchListener {
        // https://stackoverflow.com/questions/7892853/how-to-use-correct-dragging-of-a-view-on-android/18806475#18806475
        var prevX = 0
        var prevY = 0
        val MIN_DIM = 5
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v == null || event == null) { return false }
            val direction = this@AnchorView.tag
            //val anchorParams = anchorView.layoutParams as LayoutParams
            val rectangleView = this@AnchorView.rectangleView
            val rectangleParams = rectangleView.layoutParams as LayoutParams

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (direction == "left" || direction == "right") {
                        // Get the diff
                        val diff = event.rawX.toInt() - prevX
                        // Move the rect
                        if (direction == "left") {
                            val width = rectangleParams.width + diff*-1
                            if (width < MIN_DIM) { return false }
                            rectangleParams.leftMargin += diff
                            rectangleParams.width = width
                        }
                        if (direction == "right") {
                            val width = rectangleParams.width + diff
                            if (width < MIN_DIM) { return false }
                            rectangleParams.width = width
                        }
                        // Move the anchor
                        params.leftMargin += diff
                        prevX = event.rawX.toInt()
                        // Move the perpendicular anchors
                        val left = rectangleView.params.leftMargin + rectangleView.params.width / 2 - 10
                        rectangleView.anchors.top.params.leftMargin = left
                        rectangleView.anchors.bottom.params.leftMargin = left
                    }
                    if (direction == "top" || direction == "bottom") {
                        // Get the diff
                        val diff = event.rawY.toInt() - prevY
                        // Move the rect
                        if (direction == "top") {
                            val height = rectangleParams.height + diff*-1
                            if (height < MIN_DIM) { return false }
                            rectangleParams.topMargin += diff
                            rectangleParams.height = height
                        }
                        if (direction == "bottom") {
                            val height = rectangleParams.height + diff
                            if (height < MIN_DIM) { return false }
                            rectangleParams.height = height
                        }
                        // Move the anchor
                        params.topMargin += diff
                        prevY = event.rawY.toInt()
                        // Move the perpendicular anchors
                        val top = rectangleView.params.topMargin + rectangleView.params.height / 2 - 10
                        rectangleView.anchors.left.params.topMargin = top
                        rectangleView.anchors.right.params.topMargin = top
                    }
                    this@AnchorView.layoutParams = params
                    rectangleView.layoutParams = rectangleParams
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (direction == "top" || direction == "bottom") {
                        params.topMargin += event.rawY.toInt() - prevY
                    }
                    if (direction == "left" || direction == "right") {
                        params.leftMargin += event.rawX.toInt() - prevX
                        this@AnchorView.layoutParams = params
                    }
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    // Get data for anchor
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()

                    // little confused about bottom/right margins and the values??
                    params.bottomMargin = -2 * this@AnchorView.height
                    params.rightMargin = -2 * this@AnchorView.width
                    this@AnchorView.layoutParams = params

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