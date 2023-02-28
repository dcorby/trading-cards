package com.example.tradingcards

import com.example.tradingcards.db.DBManager

interface MainReceiver {
    fun getScreenDims(): HashMap<String, Float>
    fun getDBManager(): DBManager
}