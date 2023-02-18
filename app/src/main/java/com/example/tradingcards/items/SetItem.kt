package com.example.tradingcards.items

class SetItem(path: String) {
    //val itemId = "${setId}-${cardId}"
    val path = path
    val name = path.split("/").last()
    val playerName = "foo"
}