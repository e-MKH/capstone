package com.example.capstone.data.api

import com.example.capstone.data.model.GNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ✅ [GNewsService]
 * 이 인터페이스는 GNews API와 통신하기 위한 Retrofit 정의입니다.
 * Retrofit이 이 인터페이스를 기반으로 실제 네트워크 요청 코드를 자동 생성합니다.
 */
interface GNewsService {

    /**
     * ✅ [GET 요청 - top-headlines]
     * GNews API에서 'top-headlines' 엔드포인트를 호출합니다.
     *
     * @param lang  - 언어 코드 (예: "en", "ja", "zh")
     * @param topic - 기사 주제 (예: "politics", "business", "science")
     * @param token - GNews API 접근 토큰
     * @return GNewsResponse - 뉴스 기사 데이터 리스트를 포함한 응답 객체
     */
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("lang") lang: String,
        @Query("topic") topic: String,
        @Query("token") token: String
    ): GNewsResponse
}


