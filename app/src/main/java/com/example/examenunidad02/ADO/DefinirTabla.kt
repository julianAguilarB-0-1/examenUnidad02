package com.example.examenunidad02.ADO

import android.provider.BaseColumns

class DefinirTabla {
    object Libro : BaseColumns{
        const val TABLA = "libros";
        const val ID = "id";
        const val CODIGO = "codigo";
        const val AUTOR = "autor";
        const val EDITORIAL = "editorial";
        const val YEAR = "year";
    }
}