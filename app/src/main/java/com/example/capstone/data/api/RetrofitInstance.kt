package com.example.capstone.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    // ✅ lazy: 처음 사용할 때 초기화됨 (지연 초기화)
    val api: GNewsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GNewsService::class.java)
    }
}
