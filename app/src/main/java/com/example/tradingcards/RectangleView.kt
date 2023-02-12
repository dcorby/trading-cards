package com.example.tradingcards

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.example.tradingcards.ui.main.CreateDesignFragment

class RectangleView: RelativeLayout {

    var anchors: Anchors

    class Anchors(context: Context?) {
        val left = AnchorView(context)
        val top = AnchorView(context)
        val right = AnchorView(context)
        val bottom = AnchorView(context)

        fun show(init: Boolean) {
            left.show(init)
            top.show(init)
            right.show(init)
            bottom.show(init)
        }

        fun hide() {
            left.hide()
            top.hide()
            right.hide()
            bottom.hide()
        }

        fun move(axis: String, diff: Int) {
            left.move(axis, diff)
            top.move(axis, diff)
            right.move(axis, diff)
            bottom.move(axis, diff)
        }
    }

    val params = LayoutParams(100, 100)

    constructor(context: Context?, createDesignFragment: CreateDesignFragment) : super(context!!) {
        anchors = Anchors(context)
        //this.setBackgroundColor(resources.getColor(R.color.blue2))
        this.setBackgroundColor(Color.parseColor(Utils.getRandomHexCode()))
        anchors.left.tag = "left"
        anchors.top.tag = "top"
        anchors.right.tag = "right"
        anchors.bottom.tag = "bottom"
        anchors.left.setBackgroundColor(Color.parseColor("#0000FF"))
        anchors.top.setBackgroundColor(Color.parseColor("#0000FF"))
        anchors.right.setBackgroundColor(Color.parseColor("#0000FF"))
        anchors.bottom.setBackgroundColor(Color.parseColor("#0000FF"))
        anchors.left.rectangleView = this
        anchors.top.rectangleView = this
        anchors.right.rectangleView = this
        anchors.bottom.rectangleView = this

        this.setOnClickListener {
            createDesignFragment.activeView.anchors.hide()
            createDesignFragment.activeView = this
            this.anchors.show(false)
        }

        this.setOnTouchListener(onTouchListener)
    }

    fun show(origin: Pair<Int, Int>?) {
        if (origin != null) {
            params.setMargins(origin.first, origin.second, origin.first, origin.second)
        }
        this.layoutParams = params
    }

    val onTouchListener = object : View.OnTouchListener {
        var prevX = 0
        var prevY = 0
        var rectangleView = this@RectangleView
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v == null || event == null) { return false }
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val diffX = event.rawX.toInt() - prevX
                    val diffY = event.rawY.toInt() - prevY
                    params.leftMargin += diffX
                    params.topMargin += diffY
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()
                    rectangleView.layoutParams = params

                    // Get the movements to send to anchorView
                    if (prevX != 0) { rectangleView.anchors.move("x", diffX) }
                    if (prevY != 0) { rectangleView.anchors.move("y", diffY) }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()
                    params.bottomMargin = -2 * params.height
                    params.rightMargin = -2 * params.width
                    rectangleView.layoutParams = params
                    return true
                }
            }
            return false
        }
    }
}