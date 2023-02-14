package com.example.tradingcards

interface MainReceiver {
    fun getScreenDims(): HashMap<String, Int>
    fun getDefaultDesign(width: Int, height: Int): MutableList<HashMap<String, Any?>>
}