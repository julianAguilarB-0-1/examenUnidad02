package com.example.examenunidad02.ADO

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class LibroDB(private val context: Context) {
    private val dbHelper = LibroDbHelper(context)
    private lateinit var db : SQLiteDatabase
}