package com.example.capstone.data.model

/**
 * [ArticleCardItem]
 * GNews 또는 EasyNews 기사 모두를 포함할 수 있는 공통 상위 타입
 */
interface ArticleCardItem {
    val title: String
    val description: String?
    val url: String
    val publishedAt: String?
    val difficulty: String?
}

/**
 * [GNewsArticle] - GNews에서 가져온 기사 구조
 */
data class GNewsArticle(
    override val title: String,
    override val description: String?,
    override val url: String,
    val image: String?,
    override val publishedAt: String,
    override var difficulty: String? = null,
    val content: String? = null
) : ArticleCardItem

/**
 * [EasyNewsArticle] - Flask 서버에서 가져온 쉬운 뉴스 기사 구조
 */
data class EasyNewsArticle(
    override val title: String,
    override val description: String? = null, // 간략한 요약 없을 수 있음
    override val url: String,
    override val publishedAt: String? = null,
    override val difficulty: String? = "입문"
) : ArticleCardItem

