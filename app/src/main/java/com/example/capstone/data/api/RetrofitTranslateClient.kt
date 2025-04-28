package com.example.capstone.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitTranslateClient {
    val translateService: TranslateService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:7000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslateService::class.java)
    }
}