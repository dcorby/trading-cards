package com.example.tradingcards.views

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout

class ResizeView: FrameLayout {

    val DIM = 40

    lateinit var cropperView: RelativeLayout
    lateinit var resizeParams: LayoutParams

    constructor(context: Context?) : super(context!!) {
        this.setOnTouchListener(onTouchListener)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    fun show() {
        val cropperParams = cropperView.layoutParams as FrameLayout.LayoutParams
        val leftMargin = cropperParams.leftMargin + cropperParams.width - DIM / 2
        val topMargin = cropperParams.topMargin + cropperParams.height - DIM / 2
        resizeParams = FrameLayout.LayoutParams(DIM, DIM)
        resizeParams.leftMargin = leftMargin
        resizeParams.topMargin = topMargin
        this.layoutParams = resizeParams
    }

    val onTouchListener = object : View.OnTouchListener {
        // https://stackoverflow.com/questions/7892853/how-to-use-correct-dragging-of-a-view-on-android/18806475#18806475
        var prevX = 0
        var prevY = 0
        val MIN_DIM = 5
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v == null || event == null) { return false }
            val resizeView = this@ResizeView
            val cropperParams = cropperView.layoutParams as LayoutParams

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {

                    // Get the diff
                    val diffX = event.rawX.toInt() - prevX
                    val diffY = event.rawY.toInt() - prevY

                    if (diffX != 0) {
                        //if (width < MIN_DIM) { return false }
                        cropperParams.width += diffX
                        resizeParams.leftMargin += diffX
                    }

                    if (diffY != 0) {
                        //if (width < MIN_DIM) { return false }
                        cropperParams.height += diffY
                        resizeParams.topMargin += diffY
                    }

                    cropperView.layoutParams = cropperParams
                    resizeView.layoutParams = resizeParams

                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()

                    resizeParams.bottomMargin = -2 * resizeParams.height
                    resizeParams.rightMargin = -2 * resizeParams.width
                    resizeView.layoutParams = resizeParams

                    cropperParams.bottomMargin = -2 * cropperParams.height
                    cropperParams.rightMargin = -2 * cropperParams.width
                    cropperView.layoutParams = cropperParams
                    return true
                }
            }
            return false
        }
    }
}