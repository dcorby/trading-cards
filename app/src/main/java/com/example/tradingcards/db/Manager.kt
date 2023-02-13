package com.example.tradingcards.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase

class Manager(context: Context) {

    private var context: Context = context
    private lateinit var helper: Helper
    private lateinit var database: SQLiteDatabase

    @Throws(SQLException::class)
    fun open(): Manager {
        helper = Helper(context)
        // this will call oncreate() & onopen()
        database = helper.writableDatabase
        return this
    }

    fun close() {
        helper.close()
    }

    @Throws(SQLiteConstraintException::class)
    fun insert(table: String, contentValues: ContentValues): Long {
        // https://stackoverflow.com/questions/3421577/sqliteconstraintexception-not-caught
        return database.insertOrThrow(table, null, contentValues)
    }

    // https://stackoverflow.com/questions/6293063/identifying-datatype-of-a-column-in-an-sqlite-android-cursor
    fun fetch(query: String, args: Array<String>): ArrayList<HashMap<Any, Any>> {
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
    fun fetch(query: String, args: Array<String>, key: String): ArrayList<Any> {
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