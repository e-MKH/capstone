package com.example.capstone.data.api.service

import com.example.capstone.data.api.model.JapaneseNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JapaneseNlpService {
    @GET("analyze-japanese-news")
    suspend fun analyzeJapaneseNews(@Query("q") keyword: String): JapaneseNewsResponse
}