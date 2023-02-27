package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel

class DisplaySetViewModel : ViewModel() {
    var id = ""
    var currentDirectory = ""
    var ids = mutableListOf<String>()
}