package com.example.aplicaciondam2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicaciondam2.ui.CatalogoUiState
import com.example.aplicaciondam2.ui.CatalogoViewModel

class CatalogoActivity : AppCompatActivity() {

    private val viewModel: CatalogoViewModel by viewModels()

    private lateinit var adapter: CocheAdapter
    private lateinit var pbCargando: ProgressBar
    private lateinit var tvEstadoError: TextView
    private lateinit var layoutSelectorMarca: LinearLayout
    private lateinit var spMarcas: Spinner
    private lateinit var btnModoLocal: Button
    private lateinit var btnModoOnline: Button

    private var esModoOnline = false
    private val marcasDisponibles = listOf("Seat", "Toyota", "Ford", "BMW", "Volkswagen", "Honda", "Audi", "Nissan")
    private var marcaSeleccionada = "Seat"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        pbCargando = findViewById(R.id.pbCargando)
        tvEstadoError = findViewById(R.id.tvEstadoError)
        layoutSelectorMarca = findViewById(R.id.layoutSelectorMarca)
        spMarcas = findViewById(R.id.spMarcas)
        btnModoLocal = findViewById(R.id.btnModoLocal)
        btnModoOnline = findViewById(R.id.btnModoOnline)

        val rvCoches: RecyclerView = findViewById(R.id.rvCoches)
        val btnVolver: Button = findViewById(R.id.btnVolverInicio)

        // Configuración del adaptador
        adapter = CocheAdapter(
            lista = emptyList(),
            onClick = { coche -> abrirDetalle(coche) },
            onDeleteClick = { coche -> confirmarBorrado(coche) },
            onImportClick = { coche -> importarModeloAlConcesionario(coche) }
        )

        rvCoches.layoutManager = LinearLayoutManager(this)
        rvCoches.adapter = adapter

        // Configuración del Spinner de marcas para el modo online
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, marcasDisponibles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMarcas.adapter = spinnerAdapter

        spMarcas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                marcaSeleccionada = marcasDisponibles[position]
                if (esModoOnline) {
                    viewModel.cargarCatalogoOnline(marcaSeleccionada)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Cambio de pestañas / modos
        btnModoLocal.setOnClickListener { cambiarAModoLocal() }
        btnModoOnline.setOnClickListener { cambiarAModoOnline() }

        btnVolver.setOnClickListener { finish() }

        // Observar estado del ViewModel
        observarViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (esModoOnline) {
            viewModel.cargarCatalogoOnline(marcaSeleccionada)
        } else {
            viewModel.cargarCatalogoLocal()
        }
    }

    private fun cambiarAModoLocal() {
        esModoOnline = false
        layoutSelectorMarca.visibility = View.GONE
        btnModoLocal.alpha = 1.0f
        btnModoOnline.alpha = 0.6f
        viewModel.cargarCatalogoLocal()
    }

    private fun cambiarAModoOnline() {
        esModoOnline = true
        layoutSelectorMarca.visibility = View.VISIBLE
        btnModoLocal.alpha = 0.6f
        btnModoOnline.alpha = 1.0f
        viewModel.cargarCatalogoOnline(marcaSeleccionada)
    }

    private fun observarViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is CatalogoUiState.Cargando -> {
                    pbCargando.visibility = View.VISIBLE
                    tvEstadoError.visibility = View.GONE
                }
                is CatalogoUiState.Exito -> {
                    pbCargando.visibility = View.GONE
                    tvEstadoError.visibility = View.GONE
                    adapter.actualizarLista(state.coches)
                }
                is CatalogoUiState.Error -> {
                    pbCargando.visibility = View.GONE
                    tvEstadoError.visibility = View.VISIBLE
                    tvEstadoError.text = state.mensaje
                    adapter.actualizarLista(emptyList())
                }
            }
        }
    }

    private fun confirmarBorrado(coche: Coche) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar coche")
            .setMessage("¿Estás seguro de que deseas eliminar ${coche.marca} ${coche.modelo}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.borrarCoche(coche, esModoOnline, marcaSeleccionada)
                Toast.makeText(this, "Elemento eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun importarModeloAlConcesionario(coche: Coche) {
        val intent = Intent(this, AnadirCocheActivity::class.java)
        intent.putExtra("marcaPredefinida", coche.marca)
        intent.putExtra("modeloPredefinido", coche.modelo)
        startActivity(intent)
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
        intent.putExtra("imageUrl", coche.imageUrl)
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
