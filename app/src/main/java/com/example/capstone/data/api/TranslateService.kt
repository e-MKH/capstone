package com.example.capstone.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


data class TranslateRequest(
    val text: String,
    val target_language: String = "ko"
)

// 응답 데이터
data class TranslateResponse(
    val translated_text: String
)

interface TranslateService {
    @POST("translate")
    suspend fun translateText(@Body request: TranslateRequest): Response<TranslateResponse>
}