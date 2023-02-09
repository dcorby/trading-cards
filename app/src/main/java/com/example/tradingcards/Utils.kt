package com.example.tradingcards

import android.content.Context

class Utils {
    companion object {
        fun getRelativePath(context: Context, absolutePath: String) : String {
            return absolutePath.replace(context.filesDir.absolutePath, "/", false)
        }
    }
}