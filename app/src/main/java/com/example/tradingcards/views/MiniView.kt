package com.example.tradingcards.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.RelativeLayout
import kotlin.collections.HashMap

class MiniView : RelativeLayout {

    lateinit var mContext: Context
    var mAspectRatio: Float? = null

    constructor(context: Context) : super(context)
    constructor(context: Context?, design: MutableList<HashMap<String, Any?>>, miniParams: LayoutParams, aspectRatio: Float)
            : super(context!!) {

        mContext = context
        mAspectRatio = aspectRatio
        this.layoutParams = LayoutParams(miniParams.width, miniParams.height)

        design.filter{ view -> view["type"] == "ShapeView" }.forEach { viewData ->
            this.addView(getSubview(viewData))
        }
    }

    private fun getSubview(viewData: HashMap<String, Any?>) : View {

        val width = viewData.getValue("width") as Float * this.layoutParams.width
        val height = width / mAspectRatio!!
        val marginLeft = viewData.getValue("margin_left") as Float * this.layoutParams.width
        val marginTop = viewData.getValue("margin_top") as Float * this.layoutParams.height

        val params = LayoutParams(width.toInt(), height.toInt())
        params.leftMargin = marginLeft.toInt()
        params.topMargin = marginTop.toInt()

        val view = RelativeLayout(mContext)
        view.layoutParams = params
        view.setBackgroundColor(Color.parseColor(viewData.getValue("hexadecimal") as String))
        return view
    }
}