package com.example.aplicaciondam2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CocheDao {

    @Insert
    suspend fun insertar(coche: Coche)

    @Query("SELECT * FROM coches ORDER BY id DESC")
    suspend fun obtenerTodos(): List<Coche>

    @Delete
    suspend fun borrar(coche: Coche)

    @Query("SELECT COUNT(*) FROM coches")
    suspend fun contar(): Int
}
