package com.example.tradingcards.items

// A representation of a card or set within a list
// cardId will be the auto-increment SQLite value
class SourceItem(label: String, synced: Boolean) {

    //val itemId = "${setId}-${cardId}"
    //val playerName = "foo"
    val label = label
    var synced = synced
}