package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel

class ExampleViewModel : ViewModel() {

    lateinit var playerName: String
    lateinit var playerNumber: String
    lateinit var playerTeam: String
    lateinit var playerPosition: String
    lateinit var playerImage: String
    lateinit var playerBirthplace: String
    lateinit var playerBirthdate: String
    lateinit var playerHeight: String
    lateinit var playerWeight: String
    lateinit var playerBats: String
    lateinit var playerThrows: String

    lateinit var playerStats: List<HashMap<String, String>>
}