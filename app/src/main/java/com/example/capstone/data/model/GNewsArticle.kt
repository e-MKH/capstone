package com.example.capstone.data.model


data class GNewsArticle(
    val title: String,
    val description: String,
    val url: String,
    val image: String?,
    val publishedAt: String,
    var difficulty: String? = null,
    val content: String? = null
)


data class GNewsResponse(
    val articles: List<GNewsArticle>
)

