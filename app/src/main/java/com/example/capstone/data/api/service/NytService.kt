package com.example.capstone.data.api.service

import com.example.capstone.data.model.NytResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NytService {
    @GET("topstories/v2/science.json")
    suspend fun getExpertArticles(
        @Query("api-key") apiKey: String
    ): NytResponse
}
