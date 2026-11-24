package com.example.aplicaciondam2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CatalogoActivity : AppCompatActivity() {

    private lateinit var adapter: CocheAdapter
    private lateinit var cocheDao: CocheDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        cocheDao = AppDatabase.obtener(this).cocheDao()

        val rvCoches: RecyclerView = findViewById(R.id.rvCoches)
        val btnVolver: Button = findViewById(R.id.btnVolverInicio)

        // Creamos el adaptador configurando el clic sencillo (detalle) y el botón de borrado
        adapter = CocheAdapter(
            lista = emptyList(),
            onClick = { coche -> abrirDetalle(coche) },
            onDeleteClick = { coche -> confirmarBorrado(coche) }
        )

        // Lista vertical
        rvCoches.layoutManager = LinearLayoutManager(this)
        rvCoches.adapter = adapter

        // Volver a la pantalla principal
        btnVolver.setOnClickListener { finish() }
    }

    // Carga los coches desde la base de datos cada vez que la pantalla se muestra
    override fun onResume() {
        super.onResume()
        cargarCatalogo()
    }

    private fun cargarCatalogo() {
        lifecycleScope.launch {
            val lista = cocheDao.obtenerTodos()
            adapter.actualizarLista(lista)
        }
    }

    private fun confirmarBorrado(coche: Coche) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar coche")
            .setMessage("¿Estás seguro de que deseas eliminar ${coche.marca} ${coche.modelo}?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    cocheDao.borrar(coche)
                    Toast.makeText(this@CatalogoActivity, "Coche eliminado", Toast.LENGTH_SHORT).show()
                    cargarCatalogo()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirDetalle(coche: Coche) {
        val intent = Intent(this, DetalleCocheActivity::class.java)
        intent.putExtra("marca", coche.marca)
        intent.putExtra("modelo", coche.modelo)
        intent.putExtra("anio", coche.anio)
        intent.putExtra("precio", coche.precio)
        intent.putExtra("descripcion", coche.descripcion)
        intent.putExtra("imagenResId", coche.imagenResId)
        intent.putExtra("fotoPath", coche.fotoPath)
        if (coche.latitud != null && coche.longitud != null) {
            intent.putExtra("tieneUbicacion", true)
            intent.putExtra("latitud", coche.latitud)
            intent.putExtra("longitud", coche.longitud)
        } else {
            intent.putExtra("tieneUbicacion", false)
        }
        startActivity(intent)
    }
}
