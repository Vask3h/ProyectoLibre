package com.example.aplicaciondam2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AnadirCocheActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_coche)

        val etMarca: EditText = findViewById(R.id.etMarca)
        val etModelo: EditText = findViewById(R.id.etModelo)
        val etAnio: EditText = findViewById(R.id.etAnio)
        val etPrecio: EditText = findViewById(R.id.etPrecio)
        val etDescripcion: EditText = findViewById(R.id.etDescripcion)
        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        val btnCancelar: Button = findViewById(R.id.btnCancelar)

        btnGuardar.setOnClickListener {
            // Leemos los textos (trim() quita espacios al principio y al final)
            val marca = etMarca.text.toString().trim()
            val modelo = etModelo.text.toString().trim()
            val anioTexto = etAnio.text.toString().trim()
            val precioTexto = etPrecio.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()

            // 1) Campos obligatorios no vacíos
            if (marca.isEmpty() || modelo.isEmpty() || anioTexto.isEmpty() ||
                precioTexto.isEmpty() || descripcion.isEmpty()
            ) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2) Año numérico y razonable (entre 1950 y el año actual + 1)
            val anio = anioTexto.toIntOrNull()
            val anioActual = Calendar.getInstance().get(Calendar.YEAR)
            if (anio == null || anio < 1950 || anio > anioActual + 1) {
                Toast.makeText(
                    this,
                    "El año debe estar entre 1950 y ${anioActual + 1}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 3) Precio numérico y mayor que cero
            val precio = precioTexto.toDoubleOrNull()
            if (precio == null || precio <= 0) {
                Toast.makeText(this, "El precio debe ser mayor que 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Todo correcto: creamos el coche y lo guardamos en el repositorio
            val coche = Coche(marca, modelo, anio, precio, descripcion, R.drawable.ic_coche)
            CocheRepository.agregar(coche)

            Toast.makeText(this, "Coche guardado correctamente", Toast.LENGTH_SHORT).show()
            finish() // volvemos a la pantalla anterior
        }

        btnCancelar.setOnClickListener { finish() }
    }
}
