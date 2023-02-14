package com.example.tradingcards

interface MainReceiver {
    fun getScreenDims(): HashMap<String, Int?>
    fun getDefaultDesign(w: Int, h: Int): MutableList<HashMap<String, Any?>>
}