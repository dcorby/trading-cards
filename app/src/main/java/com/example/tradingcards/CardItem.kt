package com.example.tradingcards

// cardId will be the auto-increment SQLite value
class CardItem(setId: Int, cardId: Int) {

    val cardId = "${setId}-${cardId}"
}