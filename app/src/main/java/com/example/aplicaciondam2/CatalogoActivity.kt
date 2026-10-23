package com.example.aplicaciondam2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CatalogoActivity : AppCompatActivity() {

    private lateinit var adapter: CocheAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        val rvCoches: RecyclerView = findViewById(R.id.rvCoches)
        val btnVolver: Button = findViewById(R.id.btnVolverInicio)

        // Creamos el adapter con la lista compartida y qué hacer al pulsar un coche
        adapter = CocheAdapter(CocheRepository.getCoches()) { coche ->
            abrirDetalle(coche)
        }

        // Lista vertical
        rvCoches.layoutManager = LinearLayoutManager(this)
        rvCoches.adapter = adapter

        // Volver a la pantalla principal (cierra esta Activity)
        btnVolver.setOnClickListener { finish() }
    }

    // Por si hay coches nuevos al volver a esta pantalla: refrescamos la lista
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    // Envía los datos del coche a la pantalla de detalle
    private fun abrirDetalle(coche: Coche) {
        val intent = Intent(this, DetalleCocheActivity::class.java)
        intent.putExtra("marca", coche.marca)             // String
        intent.putExtra("modelo", coche.modelo)           // String
        intent.putExtra("anio", coche.anio)               // Int
        intent.putExtra("precio", coche.precio)           // Double
        intent.putExtra("descripcion", coche.descripcion) // String
        intent.putExtra("imagenResId", coche.imagenResId) // Int
        startActivity(intent)
    }
}
