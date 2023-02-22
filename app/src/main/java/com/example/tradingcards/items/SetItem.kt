package com.example.tradingcards.items

import java.io.File

class SetItem(val file: File, val isCard: Boolean, val label: String) {
    val pathname = file.toString()
    val filename = pathname.split("/").last()
}