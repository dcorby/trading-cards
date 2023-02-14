package com.example.tradingcards

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.children
import kotlin.collections.HashMap

class MiniView : RelativeLayout {

    lateinit var mContext: Context

    constructor(context: Context) : super(context)
    constructor(context: Context?, design: MutableList<HashMap<String, Any?>>)
            : super(context!!) {

        mContext = context

        design.filter{ view -> view["type"] == "ShapeView" }.forEach { viewData ->
            this.addView(convertMapToView(viewData))
        }
    }

    private fun convertMapToView(viewData: HashMap<String, Any?>) : View {

        val params = LayoutParams(viewData.getValue("width") as Int, viewData.getValue("height") as Int)
        params.leftMargin  = viewData.getValue("margin_left") as Int
        params.topMargin = viewData.getValue("margin_top") as Int

        val view = RelativeLayout(mContext)
        view.layoutParams = params
        view.setBackgroundColor(Color.parseColor(viewData.getValue("hexadecimal") as String))
        return view
    }

    fun shrink(shrinkFactor: Float) : View {
        this.children.forEach { view ->
            val params = view.layoutParams as RelativeLayout.LayoutParams
            params.width = (params.width * shrinkFactor).toInt()
            params.height = (params.height * shrinkFactor).toInt()
            params.leftMargin  = (params.leftMargin * shrinkFactor).toInt()
            params.topMargin = (params.topMargin * shrinkFactor).toInt()
            view.layoutParams = params
        }
        return this
    }
}