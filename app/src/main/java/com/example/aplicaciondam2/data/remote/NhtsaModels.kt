package com.example.aplicaciondam2.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Respuesta envoltorio devuelta por la API NHTSA VPIC.
 */
data class NhtsaModelResponse(
    @SerializedName("Count") val count: Int = 0,
    @SerializedName("Message") val message: String? = null,
    @SerializedName("SearchCriteria") val searchCriteria: String? = null,
    @SerializedName("Results") val results: List<NhtsaModelDto>? = emptyList()
)

/**
 * Representa un modelo de vehículo obtenido desde la API NHTSA.
 */
data class NhtsaModelDto(
    @SerializedName("Make_ID") val makeId: Int? = null,
    @SerializedName("Make_Name") val makeName: String = "",
    @SerializedName("Model_ID") val modelId: Int = 0,
    @SerializedName("Model_Name") val modelName: String = ""
)
