package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel
import java.io.File

class SetViewModel : ViewModel() {
    var currentDirectory = ""
    var source: String? = null
    var card = -1
    lateinit var currentSet: File
    var isHome: Boolean? = null
}