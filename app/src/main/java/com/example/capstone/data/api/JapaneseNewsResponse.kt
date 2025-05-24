package com.example.capstone.data.api

data class AnalyzedArticle(
    val title: String,
    val link: String,
    val difficulty: String,
    val score: Double
)

data class JapaneseNewsResponse(
    val articles: List<AnalyzedArticle>
)

