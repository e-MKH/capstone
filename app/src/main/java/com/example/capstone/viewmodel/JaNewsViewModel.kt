package com.example.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.api.RetrofitClient
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

    private val _selectedCategory = MutableStateFlow("politics")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val userLevel = "beginner"

    private val categoryToKeyword = mapOf(
        "politics" to "政治",
        "business" to "経済",
        "world" to "国際",
        "technology" to "技術",
        "science" to "科学",
        "entertainment" to "エンタメ",
        "health" to "健康",
        "sports" to "スポーツ"
    )

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun fetchJapaneseNews(category: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val keyword = categoryToKeyword[category] ?: "政治"
                val response = RetrofitClient.japaneseNlpService.analyzeJapaneseNews(keyword)
                val resultList = response.results

                val converted = resultList.map { article ->
                    val difficulty = when (article.analysis.level) {
                        "하 (易)" -> "초급"
                        "중 (中)" -> "중급"
                        "상 (難)" -> "고급"
                        else -> "분석불가"
                    }
                    GNewsArticle(
                        title = article.original.take(30),
                        description = "난이도 점수: ${article.analysis.score}",
                        url = "",
                        image = null,
                        publishedAt = "",
                        difficulty = difficulty,
                        content = article.original
                    )
                }

                _articles.value = converted
                filterArticlesByLevel(converted)

            } catch (e: Exception) {
                Log.e("JaNewsViewModel", "❌ 분석 실패: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun filterArticlesByLevel(all: List<GNewsArticle>) {
        val (primary, secondary) = when (userLevel) {
            "beginner" -> all.partition { it.difficulty == "초급" }
            "intermediate" -> all.partition { it.difficulty == "중급" }
            else -> all.partition { it.difficulty == "고급" }
        }
        _primaryArticles.value = primary
        _secondaryArticles.value = secondary
    }
}