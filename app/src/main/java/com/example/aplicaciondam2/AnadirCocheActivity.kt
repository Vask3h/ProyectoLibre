package com.example.aplicaciondam2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class AnadirCocheActivity : AppCompatActivity() {

    private var fotoPath: String? = null
    private var archivoFotoActual: File? = null
    private var latitud: Double? = null
    private var longitud: Double? = null

    private lateinit var ivFoto: ImageView
    private lateinit var tvUbicacion: TextView
    private lateinit var clienteUbicacion: FusedLocationProviderClient

    // 1) Permiso de cámara
    private val pedirPermisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) lanzarCamara()
        else Toast.makeText(this, "Sin permiso no puedo hacer la foto", Toast.LENGTH_SHORT).show()
    }

    // 2) Hacer foto con la cámara
    private val hacerFoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val archivo = archivoFotoActual
        if (exito && archivo != null) {
            fotoPath = archivo.absolutePath
            ivFoto.setImageBitmap(Fotos.cargar(fotoPath))
            ivFoto.visibility = android.view.View.VISIBLE
        } else {
            archivo?.delete()
        }
    }

    // 3) Elegir de la galería
    private val elegirDeGaleria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val ruta = Fotos.copiarDesdeUri(this, uri)
            if (ruta != null) {
                fotoPath = ruta
                ivFoto.setImageBitmap(Fotos.cargar(ruta))
                ivFoto.visibility = android.view.View.VISIBLE
            }
        }
    }

    // 4) Permisos de ubicación (GPS)
    private val pedirPermisosUbicacion = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val fina = resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val aproximada = resultado[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fina || aproximada) obtenerUbicacion()
        else Toast.makeText(this, "Sin permiso no puedo saber dónde estás", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_coche)

        val etMarca: EditText = findViewById(R.id.etMarca)
        val etModelo: EditText = findViewById(R.id.etModelo)
        val etAnio: EditText = findViewById(R.id.etAnio)
        val etPrecio: EditText = findViewById(R.id.etPrecio)
        val etDescripcion: EditText = findViewById(R.id.etDescripcion)
        ivFoto = findViewById(R.id.ivFoto)
        tvUbicacion = findViewById(R.id.tvUbicacion)

        // Precompletar si venimos de importar un modelo online
        val marcaPredefinida = intent.getStringExtra("marcaPredefinida")
        val modeloPredefinido = intent.getStringExtra("modeloPredefinido")
        if (!marcaPredefinida.isNullOrEmpty()) etMarca.setText(marcaPredefinida)
        if (!modeloPredefinido.isNullOrEmpty()) etModelo.setText(modeloPredefinido)

        val btnFoto: Button = findViewById(R.id.btnFoto)
        val btnGaleria: Button = findViewById(R.id.btnGaleria)
        val btnUbicacion: Button = findViewById(R.id.btnUbicacion)
        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        val btnCancelar: Button = findViewById(R.id.btnCancelar)

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this)
        val cocheDao = AppDatabase.obtener(this).cocheDao()

        btnFoto.setOnClickListener { pulsarBotonFoto() }

        btnGaleria.setOnClickListener {
            elegirDeGaleria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        btnUbicacion.setOnClickListener { pulsarBotonUbicacion() }

        btnGuardar.setOnClickListener {
            val marca = etMarca.text.toString().trim()
            val modelo = etModelo.text.toString().trim()
            val anioTexto = etAnio.text.toString().trim()
            val precioTexto = etPrecio.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()

            if (marca.isEmpty() || modelo.isEmpty() || anioTexto.isEmpty() ||
                precioTexto.isEmpty() || descripcion.isEmpty()
            ) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

            val precio = precioTexto.toDoubleOrNull()
            if (precio == null || precio <= 0) {
                Toast.makeText(this, "El precio debe ser mayor que 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val coche = Coche(
                marca = marca,
                modelo = modelo,
                anio = anio,
                precio = precio,
                descripcion = descripcion,
                imagenResId = R.drawable.ic_coche,
                fotoPath = fotoPath,
                latitud = latitud,
                longitud = longitud
            )

            lifecycleScope.launch {
                cocheDao.insertar(coche)
                Toast.makeText(this@AnadirCocheActivity, "Coche guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }

    private fun pulsarBotonFoto() {
        val permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permiso == PackageManager.PERMISSION_GRANTED) lanzarCamara()
        else pedirPermisoCamara.launch(Manifest.permission.CAMERA)
    }

    private fun lanzarCamara() {
        val archivo = Fotos.crearArchivo(this)
        archivoFotoActual = archivo
        hacerFoto.launch(Fotos.uriDe(this, archivo))
    }

    private fun tienePermisoUbicacion(): Boolean {
        val fina = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val aprox = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fina == PackageManager.PERMISSION_GRANTED || aprox == PackageManager.PERMISSION_GRANTED
    }

    private fun pulsarBotonUbicacion() {
        if (tienePermisoUbicacion()) obtenerUbicacion()
        else pedirPermisosUbicacion.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        clienteUbicacion
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion ->
                if (ubicacion != null) {
                    latitud = ubicacion.latitude
                    longitud = ubicacion.longitude
                    tvUbicacion.text = "Ubicación: " +
                            "%.4f".format(ubicacion.latitude) +
                            ", " + "%.4f".format(ubicacion.longitude)
                } else {
                    Toast.makeText(this, "No se ha podido obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
