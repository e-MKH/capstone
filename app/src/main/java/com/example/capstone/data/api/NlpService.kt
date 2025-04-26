package com.example.capstone.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import java.io.Serializable

data class NlpRequest(val text: String)

data class NlpEntity(
    val name: String,
    val type: String,
    val salience: Double,
    val metadata: Map<String, String>? = null


) : Serializable

data class NlpResponse(
    val difficulty: String?,
    val entities: List<NlpEntity>,
    val text: String = ""
)


interface NlpService {
    @POST("/analyze")
    fun analyzeText(@Body request: NlpRequest): Call<NlpResponse>
}
