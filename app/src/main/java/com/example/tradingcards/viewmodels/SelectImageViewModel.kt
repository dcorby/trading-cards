package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

class SelectImageViewModel : ViewModel() {
    var id = ""
    var name = ""
    var currentDirectory = ""
    var job: Job = Job()
}