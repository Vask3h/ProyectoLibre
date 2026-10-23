package com.example.aplicaciondam2

/**
 * Clase de datos que representa un coche del catálogo.
 * Kotlin genera automáticamente toString(), equals(), copy()...
 */
data class Coche(
    val marca: String,
    val modelo: String,
    val anio: Int,
    val precio: Double,
    val descripcion: String,
    val imagenResId: Int
)
