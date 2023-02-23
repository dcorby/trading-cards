package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel
import java.io.File

class SetViewModel : ViewModel() {
    var currentDirectory = ""
    var source: String? = ""
    lateinit var currentSet: File
}