package com.example.aplicaciondam2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvResumen: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enlazamos las vistas del XML
        tvResumen = findViewById(R.id.tvResumen)
        val btnVerCatalogo: Button = findViewById(R.id.btnVerCatalogo)
        val btnAnadirCoche: Button = findViewById(R.id.btnAnadirCoche)

        // Intent explícito: abre el catálogo
        btnVerCatalogo.setOnClickListener {
            startActivity(Intent(this, CatalogoActivity::class.java))
        }

        // Intent explícito: abre el formulario
        btnAnadirCoche.setOnClickListener {
            startActivity(Intent(this, AnadirCocheActivity::class.java))
        }
    }

    // Se ejecuta cada vez que la pantalla vuelve a ser visible
    override fun onResume() {
        super.onResume()
        tvResumen.text = "Coches registrados: ${CocheRepository.total()}"
    }
}
