package com.example.capstone.data.model

/**
 * ✅ [GNewsArticle]
 * GNews API의 기사 하나를 표현하는 데이터 클래스입니다.
 *
 * @property title       기사 제목
 * @property description 기사 내용 요약
 * @property url         기사 원문 링크
 * @property image       썸네일 이미지 URL (nullable)
 * @property publishedAt 게시 날짜/시간
 */
data class GNewsArticle(
    val title: String,
    val description: String,
    val url: String,
    val image: String?,
    val publishedAt: String
)

/**
 * ✅ [GNewsResponse]
 * GNews API에서 응답받는 전체 데이터 구조입니다.
 * articles 필드에는 GNewsArticle 리스트가 포함됩니다.
 */
data class GNewsResponse(
    val articles: List<GNewsArticle>
)

