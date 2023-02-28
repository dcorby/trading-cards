package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel

class DisplaySetViewModel : ViewModel() {
    var id = ""
    var currentDirectory = ""
    var card = -1
    var ids = mutableListOf<String>()
}