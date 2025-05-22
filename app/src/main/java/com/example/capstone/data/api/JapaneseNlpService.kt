package com.example.capstone.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JapaneseNlpService {
    @GET("analyze-japanese-news")
    fun analyzeJapaneseNews(@Query("q") keyword: String): Call<JapaneseNewsResponse>
}

