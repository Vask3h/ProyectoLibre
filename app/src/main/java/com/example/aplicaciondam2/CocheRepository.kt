package com.example.aplicaciondam2

import com.example.aplicaciondam2.data.remote.ApiService
import com.example.aplicaciondam2.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio unificado que gestiona la fuente de datos local (Room)
 * y la fuente de datos remota (Retrofit / API NHTSA).
 */
class CocheRepository(
    private val cocheDao: CocheDao,
    private val apiService: ApiService = RetrofitClient.apiService
) {

    /**
     * Obtiene los modelos de una marca concreta desde la API REST.
     * Si la petición tiene éxito, guarda/actualiza los datos en Room y los devuelve.
     * Si falla la red o no hay conexión, recupera la versión cacheada desde Room.
     */
    suspend fun obtenerModelosOnlineOEnCache(marca: String): Result<List<Coche>> =
        withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getModelsForMake(marca)
                val dtos = respuesta.results ?: emptyList()

                if (dtos.isNotEmpty()) {
                    val cochesOnline = dtos.map { dto ->
                        Coche(
                            marca = dto.makeName.ifBlank { marca }.replaceFirstChar { it.uppercase() },
                            modelo = dto.modelName.replaceFirstChar { it.uppercase() },
                            anio = 2024,
                            precio = 22000.0,
                            descripcion = "Modelo oficial de la marca ${dto.makeName} (Catálogo oficial online)",
                            imagenResId = R.drawable.ic_coche,
                            esOnline = true,
                            imageUrl = generarUrlImagenEjemplo(dto.makeName, dto.modelName)
                        )
                    }
                    // Guardamos la caché en Room
                    cocheDao.insertarLista(cochesOnline)
                    Result.success(cochesOnline)
                } else {
                    // Si la API devuelve vacíos, recuperamos los de la caché en Room
                    val cache = cocheDao.obtenerPorMarca(marca)
                    Result.success(cache)
                }
            } catch (e: Exception) {
                // Modo sin conexión o error de red: servimos la caché local guardada previamente
                val cacheLocal = cocheDao.obtenerPorMarca(marca)
                if (cacheLocal.isNotEmpty()) {
                    Result.success(cacheLocal)
                } else {
                    Result.failure(e)
                }
            }
        }

    /**
     * Obtiene los coches registrados localmente por el usuario.
     */
    suspend fun obtenerCochesLocales(): List<Coche> = withContext(Dispatchers.IO) {
        cocheDao.obtenerLocales()
    }

    /**
     * Obtiene todos los coches guardados en la BD (locales + cacheados).
     */
    suspend fun obtenerTodos(): List<Coche> = withContext(Dispatchers.IO) {
        cocheDao.obtenerTodos()
    }

    suspend fun insertarCoche(coche: Coche) = withContext(Dispatchers.IO) {
        cocheDao.insertar(coche)
    }

    suspend fun borrarCoche(coche: Coche) = withContext(Dispatchers.IO) {
        cocheDao.borrar(coche)
    }

    suspend fun contarCoches(): Int = withContext(Dispatchers.IO) {
        cocheDao.contar()
    }

    /**
     * Genera una URL ilustrativa de vehículo en alta definición utilizando la API de Unsplash.
     */
    private fun generarUrlImagenEjemplo(marca: String, modelo: String): String {
        return "https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?auto=format&fit=crop&w=600&q=80"
    }
}
