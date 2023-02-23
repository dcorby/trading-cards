package com.example.tradingcards.views

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.ui.main.CreateDesignFragment

open class PartnerView : RelativeLayout {

    lateinit var anchors: Anchors
    lateinit var createDesignFragment: CreateDesignFragment
    lateinit var params: LayoutParams

    constructor(context: Context) : super(context)
    constructor(context: Context?, mCreateDesignFragment: CreateDesignFragment, mParams: LayoutParams)
            : super(context!!) {

        anchors = Anchors(context)
        this.setBackgroundColor(Color.parseColor(Utils.getRandomHexCode()))
        anchors.left.tag = "left"
        anchors.top.tag = "top"
        anchors.right.tag = "right"
        anchors.bottom.tag = "bottom"
        anchors.left.background = ContextCompat.getDrawable(context, R.drawable.border_light_blue)
        anchors.top.background = ContextCompat.getDrawable(context, R.drawable.border_light_blue)
        anchors.right.background = ContextCompat.getDrawable(context, R.drawable.border_light_blue)
        anchors.bottom.background = ContextCompat.getDrawable(context, R.drawable.border_light_blue)
        anchors.left.partnerView = this
        anchors.top.partnerView = this
        anchors.right.partnerView = this
        anchors.bottom.partnerView = this

        createDesignFragment = mCreateDesignFragment
        params = mParams

        this.setOnTouchListener(onTouchListener)
    }

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

    fun show(center: Pair<Int, Int>?) {
        if (center != null) {
            params.setMargins(
                center.first - params.width / 2,
                center.second - params.height / 2,
                center.first - params.width / 2,
                center.second - params.height / 2
            )
        }
        this.layoutParams = params
    }

    val onTouchListener = object : View.OnTouchListener {
        var prevX = 0
        var prevY = 0
        var partnerView = this@PartnerView
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
                    partnerView.layoutParams = params

                    // Get the movements to send to anchorView
                    if (prevX != 0) { partnerView.anchors.move("x", diffX) }
                    if (prevY != 0) { partnerView.anchors.move("y", diffY) }
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
                    partnerView.layoutParams = params

                    // Switch this view to active
                    createDesignFragment.activeView.anchors.hide()
                    createDesignFragment.activeView = partnerView
                    anchors.show(false)
                    return true
                }
            }
            return false
        }
    }
}