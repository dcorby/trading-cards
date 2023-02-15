package com.example.tradingcards.items

// A representation of a card or set within a list
// cardId will be the auto-increment SQLite value
class SourceItem(setId: Int, cardId: Int) {

    val itemId = "${setId}-${cardId}"
    val playerName = "foo"
}