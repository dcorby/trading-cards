package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel

class DisplayCardViewModel : ViewModel() {
    var idx = -1
    var num = -1
    var ids = mutableListOf<String>()
    var id = ""
    var card = -1
}