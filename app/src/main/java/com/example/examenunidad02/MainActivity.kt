package com.example.examenunidad02

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.examenunidad02.ADO.Libro
import com.example.examenunidad02.ADO.LibroDB
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    private lateinit var db: LibroDB

    private lateinit var btnQR: Button
    private lateinit var txtCodigo: EditText
    private lateinit var txtAutor: EditText
    private lateinit var txtEditorial: EditText
    private lateinit var txtYear: EditText

    private lateinit var btnAgregar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnBorrar: Button

    // id del libro actualmente cargado en el formulario (0 = no existe en la BD)
    private var libroId: Int = 0

    private val barcode = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            mostrarInformacionQR(result.contents)
        } else {
            Toast.makeText(
                this,
                "El scanner fue cancelado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = LibroDB(this)
        db.openDataBase()

        iniciarComponentes()
        eventosClick()
    }

    private fun iniciarComponentes() {
        btnQR = findViewById(R.id.btnQR)
        txtCodigo = findViewById(R.id.txtCodigo)
        txtAutor = findViewById(R.id.txtAutor)
        txtEditorial = findViewById(R.id.txtEditorial)
        txtYear = findViewById(R.id.txtYear)

        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnBorrar = findViewById(R.id.btnBorrar)

        btnAgregar.isEnabled = false
        btnActualizar.isEnabled = false
        btnBorrar.isEnabled = false
    }

    private fun eventosClick() {
        btnQR.setOnClickListener {
            val opciones = ScanOptions()
            opciones.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            opciones.setPrompt("Escanear QR Libro")
            opciones.setCameraId(0)
            opciones.setBeepEnabled(true)
            opciones.setOrientationLocked(false)

            barcode.launch(opciones)
        }

        btnAgregar.setOnClickListener {
            val codigoActual = txtCodigo.text.toString().trim()

            // Volver a consultar la BD con el código actual, por si el usuario lo cambió a mano
            val libroExistente = db.getLibro(codigoActual)

            if (libroExistente.id > 0) {
                Toast.makeText(
                    this,
                    "Ya existe un libro registrado con el código $codigoActual",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val libro = Libro(
                codigo = codigoActual,
                autor = txtAutor.text.toString().trim(),
                editorial = txtEditorial.text.toString().trim(),
                year = txtYear.text.toString().trim().toIntOrNull() ?: 0
            )

            val id = db.insertarLibro(libro)

            Toast.makeText(
                this,
                "Se guardo el libro con el ID $id",
                Toast.LENGTH_SHORT
            ).show()

            libroId = id.toInt()
            btnAgregar.isEnabled = false
            btnActualizar.isEnabled = true
            btnBorrar.isEnabled = true
        }

        btnActualizar.setOnClickListener {
            val codigoActual = txtCodigo.text.toString().trim()

            // Verificar que el código actual siga correspondiendo al libro que se cargó
            val libroExistente = db.getLibro(codigoActual)

            if (libroExistente.id == 0 || libroExistente.id != libroId) {
                Toast.makeText(
                    this,
                    "El código no corresponde a un libro existente válido para actualizar",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val libro = Libro(
                id = libroId,
                codigo = codigoActual,
                autor = txtAutor.text.toString().trim(),
                editorial = txtEditorial.text.toString().trim(),
                year = txtYear.text.toString().trim().toIntOrNull() ?: 0
            )

            val filas = db.actualizarLibro(libro)

            Toast.makeText(
                this,
                "Se actualizaron $filas registro(s)",
                Toast.LENGTH_SHORT
            ).show()
        }
        btnBorrar.setOnClickListener {
            val libro = Libro(id = libroId)

            val filas = db.eliminarLibro(libro)

            Toast.makeText(
                this,
                "Se borraron $filas registro(s)",
                Toast.LENGTH_SHORT
            ).show()

            limpiarFormulario()
        }
    }

    private fun mostrarInformacionQR(jsonString: String) {
        try {
            val gson = Gson()
            val libro = gson.fromJson(jsonString, Libro::class.java)

            if (!libro.codigo.isNullOrEmpty()) {
                txtCodigo.setText(libro.codigo)
                txtAutor.setText(libro.autor)
                txtEditorial.setText(libro.editorial)
                txtYear.setText(libro.year.toString())

                val libroEnBD = db.getLibro(libro.codigo)

                if (libroEnBD.id > 0) {
                    // el código ya existe en la tabla de libros
                    libroId = libroEnBD.id
                    btnAgregar.isEnabled = false
                    btnActualizar.isEnabled = true
                    btnBorrar.isEnabled = true
                } else {
                    // el código no existe en la tabla de libros
                    libroId = 0
                    btnAgregar.isEnabled = true
                    btnActualizar.isEnabled = false
                    btnBorrar.isEnabled = false
                }
            } else {
                Toast.makeText(
                    this,
                    "No se encontró información",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error al leer QR",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun limpiarFormulario() {
        txtCodigo.text.clear()
        txtAutor.text.clear()
        txtEditorial.text.clear()
        txtYear.text.clear()

        libroId = 0
        btnAgregar.isEnabled = false
        btnActualizar.isEnabled = false
        btnBorrar.isEnabled = false
    }
}