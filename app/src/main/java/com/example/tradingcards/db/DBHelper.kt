package com.example.tradingcards.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val CREATE_CARDS = """
CREATE TABLE IF NOT EXISTS cards (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  dummy TEXT NOT NULL
);"""
const val CREATE_CARD_VIEWS = """
CREATE TABLE IF NOT EXISTS card_views (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  card INTEGER NOT NULL,
  width INTEGER NOT NULL,
  height INTEGER NOT NULL,
  margin_left INTEGER NOT NULL,
  margin_top INTEGER NOT NULL,
  hexadecimal TEXT NOT NULL,
  FOREIGN KEY(card) REFERENCES cards(id)
);"""
const val CREATE_SOURCES = """
CREATE TABLE IF NOT EXISTS sources (
  id TEXT NOT NULL,
  batch TEXT NOT NULL,
  date TEXT
);"""
const val CREATE_PLAYERS = """
CREATE TABLE IF NOT EXISTS players (
  source TEXT NOT NULL,
  id TEXT NOT NULL,
  name TEXT NOT NULL,
  PRIMARY KEY (source, id)
);
"""
const val CREATE_PLAYERS_BATCHES = """
BEGIN;
CREATE TABLE IF NOT EXISTS players_batches (
  source TEXT NOT NULL,
  id TEXT NOT NULL,
  batch TEXT,
  PRIMARY KEY (source, id, batch)
);
CREATE INDEX source_batch_idx ON players_batches (source, batch);
COMMIT;
"""
const val CREATE_SETS = """
CREATE TABLE IF NOT EXISTS sets (
  path TEXT PRIMARY KEY,
  source TEXT NOT NULL,
  design INT NOT NULL
);
"""

class Helper(context: Context) : SQLiteOpenHelper(context, "tradingCards.db", null, 1) {

    private val tables = hashMapOf<String, String>(
        "cards" to CREATE_CARDS,
        "card_views" to CREATE_CARD_VIEWS,
        "sources" to CREATE_SOURCES,
        "players" to CREATE_PLAYERS,
        "players_batches" to CREATE_PLAYERS_BATCHES,
        "sets" to CREATE_SETS
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