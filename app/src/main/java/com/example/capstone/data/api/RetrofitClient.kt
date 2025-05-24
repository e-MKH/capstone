package com.example.capstone.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.capstone.data.api.JapaneseNlpService



/**
 * [RetrofitClient]
 * Flask 서버들과 통신하는 Retrofit 클라이언트를 생성하는 객체
 * - NLP 분석용 서버 : 6000번 포트
 * - 본문 추출용 서버 : 5000번 포트
 */
object RetrofitClient {

    // NLP 분석 서버 주소 (Flask 서버: 포트 6000)
    private const val NLP_BASE_URL = "http://10.0.2.2:6000/"

    // 본문 추출 서버 주소 (Flask 서버: 포트 5000)
    private const val EXTRACT_BASE_URL = "http://10.0.2.2:5000/"

    // 일본어 난이도 분석 서버 주소 (Flask 서버: 포트 6300)
    private const val JAPANESE_NLP_BASE_URL = "http://10.0.2.2:6300/"

    // 네트워크 타임아웃이 설정된 OkHttpClient
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 시도 제한 시간
        .readTimeout(30, TimeUnit.SECONDS)    // 응답 읽기 제한 시간
        .writeTimeout(30, TimeUnit.SECONDS)   // 요청 쓰기 제한 시간
        .build()

    /**
     * NLP 분석 요청용 Retrofit 인스턴스
     * /analyze 엔드포인트와 통신
     */
    val nlpService: NlpService by lazy {
        Retrofit.Builder()
            .baseUrl(NLP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NlpService::class.java)
    }

    /**
     * 뉴스 본문 추출 요청용 Retrofit 인스턴스
     * /extract 엔드포인트와 통신
     */
    val extractService: ExtractService by lazy {
        Retrofit.Builder()
            .baseUrl(EXTRACT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExtractService::class.java)
    }

    /**
     * 일본어 기사 난이도 분석 요청용 Retrofit 인스턴스
     * /analyze-japanese-news 엔드포인트와 통신
     */

    val japaneseNlpService: JapaneseNlpService by lazy {
        Retrofit.Builder()
            .baseUrl(JAPANESE_NLP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JapaneseNlpService::class.java)
    }
}


