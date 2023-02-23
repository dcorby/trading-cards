package com.example.tradingcards.views

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

const val DIM = 40

class AnchorView: RelativeLayout {

    lateinit var partnerView: PartnerView
    // Setting this.layoutParams fails per smartcast issue. Get a reference
    lateinit var params: LayoutParams

    constructor(context: Context?) : super(context!!) {
        this.setOnTouchListener(onTouchListener)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    fun show(init: Boolean) {
        if (init) {
            params = LayoutParams(DIM, DIM)
            var leftMargin = 0
            var topMargin = 0
            when (this.tag.toString()) {
                "left" -> {
                    leftMargin = partnerView.params.leftMargin - DIM / 2
                    topMargin = partnerView.params.topMargin + partnerView.params.height / 2 - DIM / 2
                }
                "top" -> {
                    leftMargin = partnerView.params.leftMargin + partnerView.params.width / 2 - DIM / 2
                    topMargin = partnerView.params.topMargin - DIM / 2
                }
                "right" -> {
                    leftMargin = partnerView.params.leftMargin + partnerView.params.width - DIM / 2
                    topMargin = partnerView.params.topMargin + partnerView.params.height / 2 - DIM / 2
                }
                "bottom" -> {
                    leftMargin = partnerView.params.leftMargin + partnerView.params.width / 2 - DIM / 2
                    topMargin = partnerView.params.topMargin + partnerView.params.height - DIM / 2
                }
            }
            params.leftMargin = leftMargin
            params.topMargin = topMargin
            this.layoutParams = params
        }
        this.visibility = View.VISIBLE
    }

    fun hide() {
        this.visibility = View.INVISIBLE
    }

    fun move(axis: String, diff: Int) {
        if (axis == "x") {
            params.leftMargin += diff
        }
        if (axis == "y") {
            params.topMargin += diff
        }
    }

    val onTouchListener = object : View.OnTouchListener {
        // https://stackoverflow.com/questions/7892853/how-to-use-correct-dragging-of-a-view-on-android/18806475#18806475
        var prevX = 0
        var prevY = 0
        val MIN_DIM = 5
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v == null || event == null) { return false }
            val anchorView = this@AnchorView
            val side = anchorView.tag
            val partnerView = anchorView.partnerView
            val partnerParams = partnerView.layoutParams as LayoutParams

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (side == "left" || side == "right") {
                        // Get the diff
                        val diff = event.rawX.toInt() - prevX
                        // Move the rect
                        if (side == "left") {
                            val width = partnerParams.width + diff*-1
                            if (width < MIN_DIM) { return false }
                            partnerParams.leftMargin += diff
                            partnerParams.width = width
                        }
                        if (side == "right") {
                            val width = partnerParams.width + diff
                            if (width < MIN_DIM) { return false }
                            partnerParams.width = width
                        }
                        // Move the anchor
                        params.leftMargin += diff
                        prevX = event.rawX.toInt()
                        // Move the perpendicular anchors
                        val left = partnerView.params.leftMargin + partnerView.params.width / 2 - DIM / 2
                        partnerView.anchors.top.params.leftMargin = left
                        partnerView.anchors.bottom.params.leftMargin = left
                    }
                    if (side == "top" || side == "bottom") {
                        // Get the diff
                        val diff = event.rawY.toInt() - prevY
                        // Move the rect
                        if (side == "top") {
                            val height = partnerParams.height + diff*-1
                            if (height < MIN_DIM) { return false }
                            partnerParams.topMargin += diff
                            partnerParams.height = height
                        }
                        if (side == "bottom") {
                            val height = partnerParams.height + diff
                            if (height < MIN_DIM) { return false }
                            partnerParams.height = height
                        }
                        // Move the anchor
                        params.topMargin += diff
                        prevY = event.rawY.toInt()
                        // Move the perpendicular anchors
                        val top = partnerView.params.topMargin + partnerView.params.height / 2 - DIM / 2
                        partnerView.anchors.left.params.topMargin = top
                        partnerView.anchors.right.params.topMargin = top
                    }
                    anchorView.layoutParams = params
                    partnerView.layoutParams = partnerParams
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    /* Does action_up need the same implementation as action_move?
                       Or any implementation? This seems to work fine, tbh
                     */
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    // Get data for anchor
                    prevX = event.rawX.toInt()
                    prevY = event.rawY.toInt()

                    // little confused about bottom/right margins and the values??
                    params.bottomMargin = -2 * anchorView.height
                    params.rightMargin = -2 * anchorView.width
                    anchorView.layoutParams = params

                    partnerParams.bottomMargin = -2 * partnerParams.height
                    partnerParams.rightMargin = -2 * partnerParams.width
                    partnerView.layoutParams = partnerParams
                    return true
                }
            }
            return false
        }
    }
}