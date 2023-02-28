package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

class SelectPlayerViewModel : ViewModel() {
    var currentDirectory = ""
    var source = ""
    var toAdd = mutableListOf<String>()
    var toRemove = mutableListOf<String>()
    var job: Job = Job()
}