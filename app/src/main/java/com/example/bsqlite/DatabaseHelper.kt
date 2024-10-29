package com.example.bsqlite
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_db.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_NAME = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LASTNAME = "lastname"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_ADDRESS = "address"

        private const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_LASTNAME TEXT NOT NULL,
            $COLUMN_AGE INTEGER NOT NULL,
            $COLUMN_GENDER TEXT NOT NULL,
            $COLUMN_EMAIL TEXT NOT NULL,
            $COLUMN_PHONE TEXT NOT NULL,
            $COLUMN_ADDRESS TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db?.execSQL(CREATE_TABLE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
            println("DatabaseHelper: Tabla $TABLE_NAME actualizada a la versión $newVersion.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addUser(
        name: String,
        lastname: String,
        age: Int,
        gender: String,
        email: String,
        phone: String,
        address: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_ADDRESS, address)
        }

        return try {
            val result = db.insert(TABLE_NAME, null, values)
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun deleteUser(id: Int): Boolean {
        val db = writableDatabase
        return try {
            val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun updateUser(
        id: Int,
        name: String,
        lastname: String,
        age: Int,
        gender: String,
        email: String,
        phone: String,
        address: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_ADDRESS, address)
        }
        return try {
            val result = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getAllUsers(): List<Map<String, Any>> {
        val db = readableDatabase
        val usersList = mutableListOf<Map<String, Any>>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID, COLUMN_NAME, COLUMN_LASTNAME, COLUMN_AGE,
                COLUMN_GENDER, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_ADDRESS
            ),
            null, null, null, null, null
        )

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val user = mapOf(
                        COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        COLUMN_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        COLUMN_LASTNAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LASTNAME)),
                        COLUMN_AGE to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                        COLUMN_GENDER to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                        COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        COLUMN_PHONE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        COLUMN_ADDRESS to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
                    )
                    usersList.add(user)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return usersList
    }
}






