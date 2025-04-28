package com.example.capstone.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.sample.BuildConfig

object GNewsApiService {
    private const val BASE_URL = "https://gnews.io/api/v4/"

    val api: GNewsService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GNewsService::class.java)
    }

    val apiKey: String
        get() = BuildConfig.GNEWS_API_KEY
}