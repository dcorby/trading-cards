package com.example.tradingcards.items

import android.content.Context
import com.example.tradingcards.Utils

class LocationItem(context: Context, path: String) {
    val absolutePath = path
    val relativePath = context.filesDir.absolutePath + path
}