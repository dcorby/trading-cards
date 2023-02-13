package com.example.tradingcards.designviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.tradingcards.ui.main.CreateDesignFragment

class DataView: PartnerView {

    lateinit var mText: String

    constructor(context: Context) : super(context)

    constructor(context: Context?, mCreateDesignFragment : CreateDesignFragment, text: String)
            : super(context!!, mCreateDesignFragment, LayoutParams(200, 50)) {

        mText = text
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.setColor(Color.BLACK)
        paint.setStyle(Paint.Style.FILL)
        canvas?.drawPaint(paint)
        paint.setColor(Color.WHITE)
        paint.setTextSize(20.toFloat())
        canvas?.drawText("Some Text", 10.toFloat(), 25.toFloat(), paint)
    }

    fun setText(text: String) {
        mText = text
        this.invalidate()
    }
}