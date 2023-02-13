package com.example.tradingcards.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val CREATE_CARDS = """
CREATE TABLE IF NOT EXISTS cards (
  id INTEGER PRIMARY KEY NOT NULL
);
"""
const val CREATE_CARD_VIEWS = """
CREATE TABLE IF NOT EXISTS card_views (
  id INTEGER PRIMARY KEY NOT NULL,
  card INTEGER NOT NULL,
  width INTEGER NOT NULL,
  height INTEGER NOT NULL,
  margin_left INTEGER NOT NULL,
  margin_top INTEGER NOT NULL,
  hexadecimal TEXT NOT NULL
  FOREIGN KEY(card) REFERENCES cards(id)
);"""

class Helper(context: Context) : SQLiteOpenHelper(context, "tradingCards.db", null, 1) {

    private val tables = hashMapOf<String, String>(
        "cards" to CREATE_CARDS,
        "card_views" to CREATE_CARD_VIEWS
    )

    private fun createTables(db: SQLiteDatabase?) {
        if (db == null) {
            throw Exception("Could not set up database")
        }
        tables.forEach { (key, value) ->
            db.execSQL(value)
        }
    }

    override fun onOpen(db: SQLiteDatabase?) {
        createTables(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        tables.forEach { (key, value) ->
            db.execSQL("DROP TABLE IF EXISTS ${key}")
        }
        createTables(db)
    }
}