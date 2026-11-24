package com.example.aplicaciondam2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CocheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(coche: Coche)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(coches: List<Coche>)

    @Query("SELECT * FROM coches ORDER BY id DESC")
    suspend fun obtenerTodos(): List<Coche>

    @Query("SELECT * FROM coches WHERE esOnline = 0 ORDER BY id DESC")
    suspend fun obtenerLocales(): List<Coche>

    @Query("SELECT * FROM coches WHERE LOWER(marca) = LOWER(:marca) ORDER BY modelo ASC")
    suspend fun obtenerPorMarca(marca: String): List<Coche>

    @Delete
    suspend fun borrar(coche: Coche)

    @Query("SELECT COUNT(*) FROM coches")
    suspend fun contar(): Int
}
