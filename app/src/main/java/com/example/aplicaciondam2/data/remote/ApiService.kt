package com.example.aplicaciondam2.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz Retrofit para realizar peticiones REST a la API de vehículos NHTSA VPIC.
 */
interface ApiService {

    @GET("vehicles/getmodelsformake/{make}")
    suspend fun getModelsForMake(
        @Path("make") make: String,
        @Query("format") format: String = "json"
    ): NhtsaModelResponse
}
