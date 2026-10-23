package com.example.aplicaciondam2

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class DetalleCocheActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_coche)

        // Recogemos los datos enviados con putExtra()
        val marca = intent.getStringExtra("marca") ?: ""
        val modelo = intent.getStringExtra("modelo") ?: ""
        val anio = intent.getIntExtra("anio", 0)
        val precio = intent.getDoubleExtra("precio", 0.0)
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val imagenResId = intent.getIntExtra("imagenResId", R.drawable.ic_coche)

        // Mostramos los datos en pantalla
        findViewById<ImageView>(R.id.ivDetalle).setImageResource(imagenResId)
        findViewById<TextView>(R.id.tvDetalleMarcaModelo).text = "$marca $modelo"
        findViewById<TextView>(R.id.tvDetalleAnio).text = "Año: $anio"
        findViewById<TextView>(R.id.tvDetallePrecio).text =
            String.format(Locale("es", "ES"), "%,.2f €", precio)
        findViewById<TextView>(R.id.tvDetalleDescripcion).text = descripcion

        // Cierra esta pantalla y vuelve al catálogo
        findViewById<Button>(R.id.btnVolverCatalogo).setOnClickListener { finish() }
    }
}
