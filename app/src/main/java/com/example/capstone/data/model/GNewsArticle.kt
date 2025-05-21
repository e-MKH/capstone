package com.example.capstone.data.model

/**
 * [GNewsArticle]
 * GNews API에서 받아오는 뉴스 기사 하나를 표현하는 데이터 클래스
 *
 * @property title        기사 제목
 * @property description  기사 요약 (간략한 설명)
 * @property url          기사 원문 URL
 * @property image        썸네일 이미지 URL (nullable)
 * @property publishedAt  기사 게시 시간 (ISO 8601 형식)
 * @property difficulty   NLP 분석 결과로 추정된 기사 난이도 (초급/중급/고급 등) - 앱 내부에서 계산
 * @property content      전체 기사 본문 내용 (옵션 - GNews 응답에 따라 포함될 수도 있음)
 */
data class GNewsArticle(
    val title: String,
    val description: String,
    val url: String,
    val image: String?,
    val publishedAt: String,
    var difficulty: String? = null,
    val content: String? = null
)

/**
 * [GNewsResponse]
 * GNews API의 전체 응답 구조를 표현한 클래스
 * - 'articles' 필드에 기사 리스트가 담김
 */
data class GNewsResponse(
    val articles: List<GNewsArticle>
)
