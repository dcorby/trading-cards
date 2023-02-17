package com.example.tradingcards.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class DBManager(context: Context) {

    private var context: Context = context
    private lateinit var helper: Helper
    private lateinit var database: SQLiteDatabase

    //@Throws(SQLException::class)
    fun open(): DBManager {
        helper = Helper(context)
        // this will call oncreate() & onopen()
        database = helper.writableDatabase
        return this
    }

    fun close() {
        helper.close()
    }

    //@Throws(SQLiteConstraintException::class)
    fun insert(table: String, contentValues: ContentValues): Long {
        // https://stackoverflow.com/questions/3421577/sqliteconstraintexception-not-caught
        // "the INTEGER PRIMARY KEY becomes an alias for the rowid."
        // https://www.sqlite.org/rowidtable.html
        return database.insertOrThrow(table, null, contentValues)
    }

    fun update(table: String, contentValues: ContentValues, whereClause: String, whereArgs: Array<String>?): Int {
        return database.update(table, contentValues, whereClause, whereArgs)
    }

    // https://stackoverflow.com/questions/6293063/identifying-datatype-of-a-column-in-an-sqlite-android-cursor
    fun fetch(query: String, args: Array<String>?): ArrayList<HashMap<Any, Any>> {
        val cursor = database.rawQuery(query, args)
        val columns = cursor.columnCount
        val arrayList: ArrayList<HashMap<Any, Any>> = ArrayList()
        while (cursor.moveToNext()) {
            val row = HashMap<Any, Any>(columns)
            for (i in 0 until columns) {
                when (cursor.getType(i)) {
                    Cursor.FIELD_TYPE_FLOAT -> row[cursor.getColumnName(i)] = cursor.getFloat(i)
                    Cursor.FIELD_TYPE_INTEGER -> row[cursor.getColumnName(i)] = cursor.getInt(i)
                    Cursor.FIELD_TYPE_STRING -> row[cursor.getColumnName(i)] = cursor.getString(i)
                }
            }
            arrayList.add(row)
        }
        cursor.close()
        return arrayList
    }

    // Extract a column as a list, by key
    fun fetch(query: String, args: Array<String>?, key: String): ArrayList<Any> {
        val tmp = fetch(query, args)
        val list = ArrayList<Any>()
        for (i in 0 until tmp.size) {
            list.add(tmp[i][key]!!)
        }
        return list
    }

    fun exec(query: String, args: Array<String>) {
        database.execSQL(query, args)
    }

    // transactions
    fun beginTransaction() {
        database.beginTransaction()
    }

    fun commitTransaction() {
        database.setTransactionSuccessful()
    }

    fun endTransaction() {
        database.endTransaction()
    }
}