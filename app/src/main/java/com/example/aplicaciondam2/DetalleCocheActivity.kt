package com.example.aplicaciondam2

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
        val fotoPath = intent.getStringExtra("fotoPath")
        val imageUrl = intent.getStringExtra("imageUrl")
        val tieneUbicacion = intent.getBooleanExtra("tieneUbicacion", false)
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)

        // Mostramos los datos en pantalla
        val ivDetalle = findViewById<ImageView>(R.id.ivDetalle)

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_coche)
                .error(R.drawable.ic_coche)
                .into(ivDetalle)
        } else {
            val bitmap = Fotos.cargar(fotoPath)
            if (bitmap != null) {
                ivDetalle.setImageBitmap(bitmap)
            } else {
                ivDetalle.setImageResource(imagenResId)
            }
        }

        findViewById<TextView>(R.id.tvDetalleMarcaModelo).text = "$marca $modelo"
        findViewById<TextView>(R.id.tvDetalleAnio).text = "Año: $anio"
        findViewById<TextView>(R.id.tvDetallePrecio).text =
            String.format(Locale("es", "ES"), "%,.2f €", precio)
        findViewById<TextView>(R.id.tvDetalleDescripcion).text = descripcion

        val tvUbicacion = findViewById<TextView>(R.id.tvDetalleUbicacion)
        if (tieneUbicacion) {
            tvUbicacion.text = "Ubicación: " + "%.4f".format(latitud) + ", " + "%.4f".format(longitud)
        } else {
            tvUbicacion.text = "Ubicación: No registrada"
        }

        // Cierra esta pantalla y vuelve al catálogo
        findViewById<Button>(R.id.btnVolverCatalogo).setOnClickListener { finish() }
    }
}
