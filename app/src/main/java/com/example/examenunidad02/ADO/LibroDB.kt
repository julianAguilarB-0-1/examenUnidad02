package com.example.examenunidad02.ADO

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class LibroDB(private val context: Context) {
    private val dbHelper = LibroDbHelper(context)
    private lateinit var db: SQLiteDatabase

    private val leerRegistros = arrayOf(
        DefinirTabla.Libro.ID,
        DefinirTabla.Libro.CODIGO,
        DefinirTabla.Libro.AUTOR,
        DefinirTabla.Libro.EDITORIAL,
        DefinirTabla.Libro.YEAR
    )

    // abrir la base de datos
    public fun openDataBase() {
        db = dbHelper.writableDatabase
    }

    public fun insertarLibro(libro: Libro): Long {
        db = dbHelper.writableDatabase
        val value = ContentValues().apply {
            put(DefinirTabla.Libro.CODIGO, libro.codigo)
            put(DefinirTabla.Libro.AUTOR, libro.autor)
            put(DefinirTabla.Libro.EDITORIAL, libro.editorial)
            put(DefinirTabla.Libro.YEAR, libro.year)
        }
        return db.insert(DefinirTabla.Libro.TABLA, null, value)
    }

    fun actualizarLibro(libro: Libro): Int {
        db = dbHelper.writableDatabase

        val value = ContentValues().apply {
            put(DefinirTabla.Libro.CODIGO, libro.codigo)
            put(DefinirTabla.Libro.AUTOR, libro.autor)
            put(DefinirTabla.Libro.EDITORIAL, libro.editorial)
            put(DefinirTabla.Libro.YEAR, libro.year)
        }

        return db.update(
            DefinirTabla.Libro.TABLA,
            value,
            "${DefinirTabla.Libro.ID} = ?",
            arrayOf(libro.id.toString())
        )
    }
    //funcion borrar

    fun eliminarLibro(libro: Libro): Int {
        db = dbHelper.writableDatabase
        return db.delete(
            DefinirTabla.Libro.TABLA,
            "${DefinirTabla.Libro.ID} = ?",
            arrayOf(libro.id.toString())
        )
    }

    //consultas

    public fun mostrarLibros(cursor: Cursor): Libro {
        if (!cursor.isAfterLast)
            return Libro().apply {
                id = cursor.getInt(0)
                codigo = cursor.getString(1)
                autor = cursor.getString(2)
                editorial = cursor.getString(3)
                year = cursor.getInt(4)
            }
        else return Libro()
    }

    public fun getLibro(codigo: String): Libro {
        db = dbHelper.readableDatabase

        val cursor = db.query(
            DefinirTabla.Libro.TABLA,
            leerRegistros,
            "${DefinirTabla.Libro.CODIGO} = ?",
            arrayOf(codigo),
            null,
            null,
            null)
        cursor.moveToFirst()
        val libro = mostrarLibros(cursor)
        return libro
    }

    public fun leerTodos(): ArrayList<Libro> {
        val db = dbHelper.writableDatabase
        val cursor = db.query(
            DefinirTabla.Libro.TABLA,
            leerRegistros,
            null,
            null,
            null,
            null,
            null,
            null)
        val listLibros = ArrayList<Libro>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val libro = mostrarLibros(cursor)
            listLibros.add(libro)
            cursor.moveToNext()
        }
        cursor.close()
        return listLibros
    }

    public fun closeDatabase() {
        dbHelper.close()
    }

}