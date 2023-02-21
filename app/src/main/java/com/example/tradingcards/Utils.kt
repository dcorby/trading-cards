package com.example.tradingcards

import android.content.Context
import android.content.res.AssetManager
import android.util.DisplayMetrics
import android.util.Log
import android.widget.RelativeLayout.LayoutParams
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.items.SetItem
import java.io.File

class Utils {
    companion object {
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

        fun readAssetsFile(context: Context, filename: String): String {
            val assetManager: AssetManager = context.getAssets()
            return assetManager.open(filename).bufferedReader().use { it.readText() }
        }

        fun getSetItems(context: Context, dbManager: DBManager, source: String?, path: File) : MutableList<SetItem> {
            val setItems = mutableListOf<SetItem>()
            val list = path.listFiles()
            if (list == null) {
                return setItems
            }
            list.forEach loop@ { file ->
                // /images is a special folder
                if (file.toString().endsWith("/images")) {
                    return@loop
                }

                val isCard = file.extension == "jpg"

                val label = if (isCard) {
                    val id = file.toString().split("/").last().replace(".jpg", "")
                    dbManager.fetch(
                        "SELECT * FROM players WHERE source = ? AND id = ?",
                        arrayOf(source!!, id),
                        "name")[0].toString()
                } else {
                    file.toString().split("/").last()
                }

                val setItem = SetItem(file, isCard, label)
                setItems.add(setItem)
            }
            return setItems
        }

        fun convertDpToPx(context: Context, dp: Float) : Float {
            return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun convertPxToDp(context: Context, px: Float) : Float {
            return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}