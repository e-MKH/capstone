package com.example.capstone.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JapaneseNlpService {
    @GET("japan-news-analyze")
    fun getAnalyzedJapaneseNews(
        @Query("category") category: String
    ): Call<JapaneseNewsResponse>
}

