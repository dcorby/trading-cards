package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

class SaveImageViewModel : ViewModel() {
    var id = ""
    var name = ""
    var link = ""
    var width = 0.toFloat()
    var height = 0.toFloat()
    var currentDirectory = ""
    var job: Job = Job()
}