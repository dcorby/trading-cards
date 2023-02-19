package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SelectPlayerViewModel : ViewModel() {
    var currentDirectory = ""
    var job: Job = Job()
}