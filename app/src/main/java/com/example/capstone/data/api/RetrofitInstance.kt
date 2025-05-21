package com.example.capstone.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * [RetrofitInstance]
 * GNews API에 요청을 보내기 위한 Retrofit 인스턴스를 생성하는 객체
 * 싱글톤 패턴으로 앱 전역에서 재사용됨
 */
object RetrofitInstance {

    /**
     * GNewsService 구현체 (lazy 초기화)
     * - 실제 네트워크 통신 객체는 최초 사용 시점에 생성됨
     * - GNews API의 Base URL은 "https://gnews.io/api/v4/"
     * - 응답 JSON은 Gson으로 파싱
     */
    val api: GNewsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/") // GNews API 기본 엔드포인트
            .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 GSON 설정
            .build()
            .create(GNewsService::class.java) // GNewsService 인터페이스와 연결
    }
}
