package com.example.tradingcards

import android.content.Context
import android.graphics.Color
import android.widget.RelativeLayout

class RectangleView: RelativeLayout {

    var anchors: Anchors

    class Anchors(context: Context?) {
        val left = AnchorView(context)
        val top = AnchorView(context)
        val right = AnchorView(context)
        val bottom = AnchorView(context)

        fun show() {
            left.show()
            top.show()
            right.show()
            bottom.show()
        }

        fun hide() {
            left.hide()
            top.hide()
            right.hide()
            bottom.hide()
        }
    }

    val params = LayoutParams(100, 100)

    constructor(context: Context?) : super(context!!) {
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
    }

    fun show(origin: Pair<Int, Int>?) {
        if (origin != null) {
            params.setMargins(origin.first, origin.second, origin.first, origin.second)
        }
        this.layoutParams = params
    }
}