package com.example.examenunidad02

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.examenunidad02.ADO.Libro
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract

class MainActivity : AppCompatActivity() {
    private lateinit var btnQR: Button
    private lateinit var txtCodigo: EditText
    private lateinit var txtAutor: EditText
    private lateinit var txtEditorial: EditText
    private lateinit var txtYear: EditText

    private lateinit var btnAgregar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnBorrar: Button

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
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        iniciarComponentes()
        eventosClick()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

}