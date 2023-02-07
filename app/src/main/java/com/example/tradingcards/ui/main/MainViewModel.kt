package com.example.tradingcards.ui.main

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

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