package com.example.capstone.data.api

import com.example.capstone.data.model.GNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface GNewsService {


    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("lang") lang: String,
        @Query("topic") topic: String,
        @Query("token") token: String
    ): GNewsResponse
}


