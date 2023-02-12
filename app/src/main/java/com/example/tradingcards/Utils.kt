package com.example.tradingcards

import android.content.Context

class Utils {
    companion object {
        fun getRelativePath(context: Context, absolutePath: String) : String {
            return absolutePath.replace(context.filesDir.absolutePath, "/", false)
        }

        fun getRandomHexCode() : String {
            val list = listOf("#831576", "#75a354", "#10933f", "#95e8db", "#a399dc", "#edf76a", "#b6fc8f", "#6e910e", "#31bb33", "#cc7484", "#4abf78", "#819248", "#c18bd3", "#3eafd9", "#957f92", "#7c8292", "#107fa0")
            return list.random()
        }
    }
}