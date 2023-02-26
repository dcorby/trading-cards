package com.example.tradingcards.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout

class CropperView: RelativeLayout {

    lateinit var resizeView: ResizeView

    constructor(context: Context) : super(context) {
        this.setOnTouchListener(onTouchListener)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.setOnTouchListener(onTouchListener)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.setOnTouchListener(onTouchListener)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    val onTouchListener = object : View.OnTouchListener {
        // https://stackoverflow.com/questions/7892853/how-to-use-correct-dragging-of-a-view-on-android/18806475#18806475
        var prevX = 0
        var prevY = 0
        var cropperParams: FrameLayout.LayoutParams? = null
        var resizeParams: FrameLayout.LayoutParams? = null
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v == null || event == null) { return false }
            cropperParams = v.layoutParams as FrameLayout.LayoutParams
            resizeParams = resizeView.layoutParams as FrameLayout.LayoutParams

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    // Get the diff
                    val diffX = event.rawX.toInt() - prevX
                    val diffY = event.rawY.toInt() - prevY

                    // Move the Cropper View
                    cropperParams!!.leftMargin += diffX
                    cropperParams!!.topMargin += diffY

                    // Move the Resize View
                    resizeParams!!.leftMargin += diffX
                    resizeParams!!.topMargin += diffY

                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()
                    v.layoutParams = cropperParams
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()

                    // little confused about bottom/right margins and the values??
                    cropperParams!!.bottomMargin = -2 * v.height
                    cropperParams!!.rightMargin = -2 * v.width
                    v.layoutParams = cropperParams
                    return true
                }
            }
            return false
        }
    }
}