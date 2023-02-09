package com.example.tradingcards.items

import android.content.Context
import com.example.tradingcards.Utils

class LocationItem(context: Context, absolutePath: String) {
    val absolutePath = absolutePath
    val relativePath = Utils.getRelativePath(context, absolutePath)
}