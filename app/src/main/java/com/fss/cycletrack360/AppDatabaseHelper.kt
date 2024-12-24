package com.fss.cycletrack360

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "cycle_computer.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE user (id INTEGER PRIMARY KEY, gender TEXT, height REAL, weight REAL, birthdate TEXT)")
        db.execSQL("CREATE TABLE activity (id INTEGER PRIMARY KEY, type TEXT, duration REAL, distance REAL, calories REAL, date TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addUser(gender: String, height: Float, weight: Float, birthdate: String) {
        val db = writableDatabase
        db.execSQL("INSERT INTO user (gender, height, weight, birthdate) VALUES (?, ?, ?, ?)", arrayOf(gender, height, weight, birthdate))
        db.close()
    }
}
