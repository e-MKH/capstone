package com.example.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.api.RetrofitClient
import com.example.capstone.data.api.model.ArticleResult
import com.example.capstone.data.model.GNewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JaNewsViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _articles = MutableStateFlow<List<GNewsArticle>>(emptyList())
    val articles: StateFlow<List<GNewsArticle>> = _articles

    private val _primaryArticles = MutableStateFlow<List<GNewsArticle>>(emptyList())
    val primaryArticles: StateFlow<List<GNewsArticle>> = _primaryArticles

    private val _secondaryArticles = MutableStateFlow<List<GNewsArticle>>(emptyList())
    val secondaryArticles: StateFlow<List<GNewsArticle>> = _secondaryArticles

    private val _selectedCategory = MutableStateFlow("政治")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val userLevel = "beginner" // TODO: 사용자 기반 레벨 자동 설정 예정


    private val categoryToKeyword = mapOf(
        "政治" to "politics",
        "経済" to "business",
        "国際" to "world",
        "技術" to "technology",
        "科学" to "science",
        "エンタメ" to "entertainment",
        "健康" to "health",
        "スポーツ" to "sports"
    )

    private val articleCache = mutableMapOf<String, List<GNewsArticle>>()
    private val cacheTimestamps = mutableMapOf<String, Long>()
    private val CACHE_TTL_MS = 10 * 60 * 1000L // 10분

    fun setCategory(category: String) {
        _selectedCategory.value = category
        fetchJapaneseNews(category, forceRefresh = true)
    }

    fun fetchJapaneseNews(category: String, forceRefresh: Boolean = false) {
        _selectedCategory.value = category
        _isLoading.value = true

        val keyword = categoryToKeyword[category] ?: "politics"
        val cacheKey = "ja|$keyword"
        val now = System.currentTimeMillis()
        val lastFetched = cacheTimestamps[cacheKey]

        val cacheValid = !forceRefresh &&
                articleCache.containsKey(cacheKey) &&
                lastFetched != null &&
                now - lastFetched < CACHE_TTL_MS

        if (cacheValid) {
            val cached = articleCache[cacheKey] ?: emptyList()
            _articles.value = cached
            filterArticlesByLevel(cached)
            _isLoading.value = false
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.japaneseNlpService.analyzeJapaneseNews(keyword)
                val converted = convertToGNewsArticles(response.results)

                _articles.value = converted
                articleCache[cacheKey] = converted
                cacheTimestamps[cacheKey] = now
                filterArticlesByLevel(converted)

            } catch (e: Exception) {
                Log.e("JaNewsViewModel", "❌ 분석 실패: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun convertToGNewsArticles(results: List<ArticleResult>): List<GNewsArticle> {
        return results.map { article ->
            val fallbackDescription = article.original
                .split(Regex("[。．！？]"))
                .take(2)
                .joinToString("。") + "。"

            Log.d("Convert", "기사 제목: ${article.original.take(20)}... | 난이도: ${article.analysis.level}")

            GNewsArticle(
                title = article.original.take(50),
                description = article.description?.takeIf { it.isNotBlank() } ?: fallbackDescription,
                url = article.url,
                image = null,
                publishedAt = article.publishedAt ?: "",
                difficulty = article.analysis.level,
                content = article.original
            )
        }
    }

    private fun filterArticlesByLevel(all: List<GNewsArticle>) {
        val validArticles = all.filter {
            it.difficulty == "초급" || it.difficulty == "중급" || it.difficulty == "고급"
        }

        val counts = validArticles.groupingBy { it.difficulty }.eachCount()
        Log.d("Filter", "난이도 분포: $counts")

        val (primary, secondary) = when (userLevel) {
            "beginner" -> validArticles.partition { it.difficulty == "초급" }
            "intermediate" -> validArticles.partition { it.difficulty == "중급" }
            else -> validArticles.partition { it.difficulty == "고급" }
        }

        Log.d("Filter", "primary: ${primary.size}개, secondary: ${secondary.size}개")

        _primaryArticles.value = primary
        _secondaryArticles.value = secondary
    }
}
