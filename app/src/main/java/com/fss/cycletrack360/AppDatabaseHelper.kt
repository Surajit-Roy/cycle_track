package com.fss.cycletrack360

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USER = "User"
        private const val COLUMN_ID = "id"
        private const val COLUMN_BIRTH_YEAR = "birth_year"
        private const val COLUMN_HEIGHT = "height"
        private const val COLUMN_WEIGHT = "weight"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_UNIT = "unit"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BIRTH_YEAR TEXT,
                $COLUMN_HEIGHT REAL,
                $COLUMN_WEIGHT REAL,
                $COLUMN_GENDER TEXT,
                $COLUMN_UNIT TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // Method to add a new user
    fun addUser(birthYear: String, height: Float, weight: Float, gender: String, unit: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BIRTH_YEAR, birthYear)
            put(COLUMN_HEIGHT, height)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_GENDER, gender)
            put(COLUMN_UNIT, unit)
        }
        db.insert(TABLE_USER, null, values)
        db.close()
    }

    // Method to fetch a user's data based on their ID
    fun getUser(userId: Int): User? {
        val db = readableDatabase
        var user: User? = null
        val cursor: Cursor = db.query(
            TABLE_USER,
            null, // Select all columns
            "$COLUMN_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                birthYear = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_YEAR)),
                height = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // Method to update a user's data based on their ID
    fun updateUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BIRTH_YEAR, user.birthYear)
            put(COLUMN_HEIGHT, user.height)
            put(COLUMN_WEIGHT, user.weight)
            put(COLUMN_GENDER, user.gender)
            put(COLUMN_UNIT, user.unit)
        }
        val rowsUpdated = db.update(
            TABLE_USER,
            values,
            "$COLUMN_ID = ?",
            arrayOf(user.id.toString())
        )
        db.close()
        return rowsUpdated > 0
    }

    // Define a User data class to encapsulate user data
    data class User(
        val id: Int,
        val birthYear: String,
        val height: Float,
        val weight: Float,
        val gender: String,
        val unit: String
    )
}
