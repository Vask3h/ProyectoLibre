package com.example.aplicaciondam2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coches")
data class Coche(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val precio: Double,
    val descripcion: String,
    val imagenResId: Int = R.drawable.ic_coche,
    val fotoPath: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)
