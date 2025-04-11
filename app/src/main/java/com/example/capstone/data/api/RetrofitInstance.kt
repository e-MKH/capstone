package com.example.capstone.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ✅ [RetrofitInstance]
 * GNewsService 인터페이스를 기반으로 실제 네트워크 요청을 수행하는 Retrofit 인스턴스를 생성합니다.
 * 싱글톤으로 선언되어 앱 전역에서 재사용됩니다.
 */
object RetrofitInstance {

    // ✅ lazy: 처음 사용할 때 초기화됨 (지연 초기화)
    val api: GNewsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/") // ✅ GNews API 기본 URL
            .addConverterFactory(GsonConverterFactory.create()) // ✅ JSON 파싱을 위한 GSON 설정
            .build()
            .create(GNewsService::class.java) // ✅ GNewsService 인터페이스와 연결
    }
}
