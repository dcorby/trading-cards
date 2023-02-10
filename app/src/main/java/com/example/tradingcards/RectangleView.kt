package com.example.tradingcards

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.marginTop

class RectangleView: RelativeLayout {

    var anchors: Anchors
    class Anchors(context: Context?) {
        val left = SquareView(context)
        val top = SquareView(context)
        val right = SquareView(context)
        val bottom = SquareView(context)
    }
    SHOULD HAVE THESE REFERENCES, BUT THEY SHOULDN'T BE CHILDREN. TOO MANY RENDERING ISSUES
    JUST AS IT MOVES +/-/X/Y, USE THE REFERENCE TO THE PARENT (BUT NOT *VIEW PARENT*) AND CHANGE ITS SIZE
    CAN STILL CONTROL THE ANCHOR VISIBILITY FROM THE ACTIVE VIEW THIS WAY

    val params = LayoutParams(100, 100)

    constructor(context: Context?) : super(context!!) {
        anchors = Anchors(context)
        this.setBackgroundColor(resources.getColor(R.color.black))
        anchors.left.tag = "left"
        anchors.top.tag = "top"
        anchors.right.tag = "right"
        anchors.bottom.tag = "bottom"
        anchors.left.setBackgroundColor(Color.parseColor("#0000FF"))
        anchors.top.setBackgroundColor(Color.parseColor("#0000FF"))
        anchors.right.setBackgroundColor(Color.parseColor("#0000FF"))
        anchors.bottom.setBackgroundColor(Color.parseColor("#0000FF"))
        this.addView(anchors.left)
        this.addView(anchors.top)
        this.addView(anchors.right)
        this.addView(anchors.bottom)
        anchors.left.parent = this
        anchors.top.parent = this
        anchors.right.parent = this
        anchors.bottom.parent = this
        anchors.left.show()
        anchors.top.show()
        anchors.right.show()
        anchors.bottom.show()
        this.clipChildren = false
    }

    fun show(origin: Pair<Int, Int>?) {
        if (origin != null) {
            params.setMargins(origin.first, origin.second, origin.first, origin.second)
        }
        this.layoutParams = params
        this.clipChildren = false
    }
}