package com.example.tradingcards

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout.LayoutParams
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat.OrientationMode
import androidx.core.content.ContextCompat
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.items.SetItem
import java.io.File

class Utils {
    companion object {
        fun getRandomHexCode() : String {
            val list = listOf("#831576", "#75a354", "#10933f", "#95e8db", "#a399dc", "#edf76a", "#b6fc8f", "#6e910e", "#31bb33", "#cc7484", "#4abf78", "#819248", "#c18bd3", "#3eafd9", "#957f92", "#7c8292", "#107fa0")
            return list.random()
        }

        fun getLayoutParams(key: String, screenDims: HashMap<String, Float>): LayoutParams {
            when(key) {
                "full" -> {
                    val params = LayoutParams(
                        screenDims.getValue("width").toInt(),
                        screenDims.getValue("height").toInt())
                    params.leftMargin = 0
                    params.topMargin = 0
                    return params
                }
                "design" -> {
                    val density = screenDims.getValue("density").toFloat()
                    val potentialWidth = screenDims.getValue("width") - dpToPx(density,32)
                    val potentialHeight = (screenDims.getValue("height") - screenDims.getValue("toolbar_height")
                                            - dpToPx(density,32) - dpToPx(density,50)).toFloat()
                    var width = potentialWidth
                    var height = screenDims.getValue("height") * width / screenDims.getValue("width")
                    if (height > potentialHeight) {
                        width = width * potentialHeight / height
                        height = potentialHeight
                    }
                    val params = LayoutParams(width.toInt(), height.toInt())
                    params.leftMargin = ((potentialWidth - width) / 2).toInt()
                    params.topMargin = ((potentialHeight - height) / 2).toInt()
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

        fun dpToPx(density: Float, dp: Int) : Float {
            return dp * density
        }

        fun pxToDp(density: Float, px: Int) : Float {
            return px / density
        }

        fun getTitleView(context: Context, currentDirectory: String, title: String?): ViewGroup {
            val linearLayout = LinearLayout(context)
            val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            linearLayout.layoutParams = params
            linearLayout.tag = "title"
            linearLayout.orientation = LinearLayout.HORIZONTAL

            val homeDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_home_32)
            val imageView = ImageView(context)
            imageView.setImageDrawable(homeDrawable)
            linearLayout.addView(imageView)

            currentDirectory.trim('/').split("/").forEach loop@ { text ->
                if (text == "") {
                    return@loop
                }
                Log.v("TEST", "text=${text}")
                val arrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_right_32)
                val imageView = ImageView(context)
                imageView.setImageDrawable(arrowDrawable)
                linearLayout.addView(imageView)

                val textView = TextView(context)
                textView.text = text
                textView.setTextColor(Color.WHITE)
                textView.textSize = 22.toFloat()
                textView.gravity = Gravity.CENTER_VERTICAL
                linearLayout.addView(textView)
            }
            if (title != null) {
                val textView = TextView(context)
                textView.text = title
                textView.setTextColor(Color.WHITE)
                textView.textSize = 22.toFloat()
                textView.gravity = Gravity.RIGHT
                val params = LinearLayout.LayoutParams(
                    0,
                    LayoutParams.WRAP_CONTENT,
                    999.0f
                )
                textView.layoutParams = params
                linearLayout.addView(textView)
            }
            return linearLayout
        }
    }
}