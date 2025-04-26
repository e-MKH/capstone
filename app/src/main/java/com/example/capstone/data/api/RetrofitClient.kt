package com.example.capstone.data.api

<<<<<<< HEAD
class RetrofitClient {
}
=======
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:6000" // ← 에뮬레이터 기준 로컬호스트

    val nlpService: NlpService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NlpService::class.java)
    }
}
>>>>>>> 7e1ef8bb2f88217eec3f51966f580b6a5f761385
