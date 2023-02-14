package com.example.tradingcards

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import kotlin.collections.HashMap

class DesignMiniView : RelativeLayout {

    lateinit var mContext: Context
    lateinit var mScreenDims: HashMap<String, Int>

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
        Log.v("TEST", "Returning view from convertMapToView()")
        Log.v("TEST", "View width=${params.width}, height=${params.height}, leftMargin=${params.leftMargin}, topMargin=${params.topMargin}")
        return view
    }

    fun shrink(shrinkFactor: Float) : View {
        this.children.forEach { view ->
            val params = view.layoutParams as RelativeLayout.LayoutParams
            params.width = (params.width * shrinkFactor).toInt()
            params.height = (params.height * shrinkFactor).toInt()
            params.leftMargin  = (params.leftMargin * shrinkFactor).toInt()
            params.topMargin = (params.topMargin * shrinkFactor).toInt()
            Log.v("TEST", "Shrunk view width=${params.width}, height=${params.height}, leftMargin=${params.leftMargin}, topMargin=${params.topMargin}")
            view.layoutParams = params
        }
        return this
    }
}