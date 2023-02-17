package com.example.tradingcards.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tradingcards.MiniView
import java.lang.ref.WeakReference

class CreateSetViewModel : ViewModel() {

    var name = ""
    var location = ""
    var currentDirectory = ""
    var absolutePath = ""
    var activeDesign = 0
}