package com.example.capstone.data.model
import com.google.gson.annotations.SerializedName
/**
 * [ArticleCardItem]
 * GNews / EasyNews / NYT 기사 모두를 포함할 수 있는 공통 상위 타입
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
 * [NytArticle] - 뉴욕타임스에서 가져온 전문가용 기사 구조
 */
data class NytArticle(
    @SerializedName("title")
    override val title: String,

    @SerializedName("item_type")
    val item_type: String,

    @SerializedName("abstract")
    val abstract: String,

    @SerializedName("url")
    override val url: String,

    @SerializedName("byline")
    val byline: String,

    @SerializedName("published_date")
    val published_date: String,

    @SerializedName("multimedia")
    val multimedia: List<NytMedia>?,

    override val difficulty: String = "전문가"
) : ArticleCardItem {
    override val description: String? get() = abstract
    override val publishedAt: String? get() = published_date
}

data class NytMedia(
    @SerializedName("url")
    val url: String,

    @SerializedName("format")
    val format: String
)