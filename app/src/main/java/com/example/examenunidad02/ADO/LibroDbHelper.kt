package com.example.examenunidad02.ADO

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LibroDbHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(SQL_CREATE_LIBRO)
    }

    override fun onUpgrade(
        p0: SQLiteDatabase?,
        p1: Int,
        p2: Int) {
        p0?.execSQL(SQL_DELETE_LIBRO)
        p0?.execSQL(SQL_CREATE_LIBRO)
    }

    companion object{
        private const val DATABASE_NAME = "libros"
        private const val DATABASE_VERSION = 1
        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"
        private const val COMA = " ,"

        private const val SQL_CREATE_LIBRO  = "CREATE TABLE "+
                "${DefinirTabla.Libro.TABLA}"+
                "(${DefinirTabla.Libro.ID} $INTEGER_TYPE PRIMARY KEY AUTOINCREMENT $COMA"+
                "${DefinirTabla.Libro.CODIGO} $TEXT_TYPE $COMA"+
                "${DefinirTabla.Libro.AUTOR} $TEXT_TYPE $COMA"+
                "${DefinirTabla.Libro.EDITORIAL} $TEXT_TYPE $COMA"+
                "${DefinirTabla.Libro.YEAR} $INTEGER_TYPE "+")"

        private const val SQL_DELETE_LIBRO = "DELETE FROM " + "${DefinirTabla.Libro.TABLA}"

    }
}