package com.example.tradingcards

// A representation of the card within a list
// cardId will be the auto-increment SQLite value
class CardItem(setId: Int, cardId: Int) {

    val cardId = "${setId}-${cardId}"
    val playerName = "foo"
}