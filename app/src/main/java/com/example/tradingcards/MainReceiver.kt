package com.example.tradingcards

import com.example.tradingcards.db.DBManager

interface MainReceiver {
    fun getScreenDims(): HashMap<String, Float>
    fun getDefaultDesign(width: Int, height: Int): ArrayList<HashMap<String, Any?>>
    fun getDBManager(): DBManager
}