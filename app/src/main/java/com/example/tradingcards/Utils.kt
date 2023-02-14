package com.example.tradingcards

import android.content.Context
import android.widget.RelativeLayout.LayoutParams

class Utils {
    companion object {
        fun getRelativePath(context: Context, absolutePath: String) : String {
            return absolutePath.replace(context.filesDir.absolutePath, "/", false)
        }

        fun getRandomHexCode() : String {
            val list = listOf("#831576", "#75a354", "#10933f", "#95e8db", "#a399dc", "#edf76a", "#b6fc8f", "#6e910e", "#31bb33", "#cc7484", "#4abf78", "#819248", "#c18bd3", "#3eafd9", "#957f92", "#7c8292", "#107fa0")
            return list.random()
        }

        fun getLayoutParams(key: String, screenDims: HashMap<String, Int>): LayoutParams {
            when(key) {
                "full" -> {
                    val params = LayoutParams(screenDims.getValue("width"), screenDims.getValue("height"))
                    params.leftMargin = 0
                    params.topMargin = 0
                    return params
                }
                "design" -> {
                    val potentialWidth = screenDims.getValue("width") - 32
                    val potentialHeight = screenDims.getValue("height") - 100 - 32 - 50
                    var width = potentialWidth
                    var height = (screenDims.getValue("height") * (width / screenDims.getValue("width").toFloat())).toInt()
                    if (height > potentialHeight) {
                        width = (width * (potentialHeight / height.toFloat())).toInt()
                        height = potentialHeight
                    }
                    val params = LayoutParams(width, height)
                    params.leftMargin = (potentialWidth - width) / 2
                    params.topMargin = (potentialHeight - height) / 2
                    params.rightMargin = params.leftMargin // why is this necessary?
                    params.bottomMargin = params.topMargin
                    return params
                }
                "mini" -> {
                    val width = 150
                    val height = (screenDims.getValue("height") * (150.toFloat() / screenDims.getValue("width"))).toInt()
                    val params = LayoutParams(width, height)
                    params.leftMargin = 0
                    params.topMargin = 0
                    return params
                }
                else -> throw Exception("getLayoutParams() invalid key")
            }
        }
    }
}