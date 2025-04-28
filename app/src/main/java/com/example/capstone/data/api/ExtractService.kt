package com.example.capstone.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface ExtractService {


    @POST("analyze")
    suspend fun analyzeText(@Body requestBody: Map<String, String>): Response<NlpResponse>


    @POST("extract")
    suspend fun extractArticle(@Body urlMap: Map<String, String>): Response<ExtractResponse>
}


data class ExtractResponse(
    val url: String,
    val text: String
)