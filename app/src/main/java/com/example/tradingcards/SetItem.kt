package com.example.tradingcards

// A representation of a card or set within a list
// cardId will be the auto-increment SQLite value
class SetItem(setId: Int, cardId: Int) {

    val itemId = "${setId}-${cardId}"
    val playerName = "foo"
}