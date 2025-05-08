package com.example.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.api.model.ExtractResponse
import com.example.capstone.data.api.service.NlpRequest
import com.example.capstone.data.api.model.NlpResponse
import com.example.capstone.data.api.service.GNewsApiService
import com.example.capstone.data.api.RetrofitClient
import com.example.capstone.data.model.GNewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * [NewsViewModel]
 * 뉴스 기사 조회 + 본문 추출 + NLP 난이도 분석을 관리하는 ViewModel
 */
class NewsViewModel : ViewModel() {

    /** 로딩 상태 - UI에서 로딩 인디케이터 표시용 */
    val isLoading = MutableStateFlow(true)

    /** 기사 리스트를 저장하는 상태 */
    private val _articles = MutableStateFlow<List<GNewsArticle>>(emptyList())
    val articles: StateFlow<List<GNewsArticle>> = _articles

    /** NLP 분석 결과를 캐싱 (URL 기반) → 중복 분석 방지 */
    private val difficultyCache = mutableMapOf<String, String>()

    /** 현재 선택된 카테고리 (기본값: "politics") */
    private val _selectedCategory = MutableStateFlow("politics")
    val selectedCategory: StateFlow<String> = _selectedCategory

    /** 현재 언어 코드 (화면 구성에 활용 가능) */
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage

    /**
     * 뉴스 기사 불러오기 + NLP 분석 시작
     * @param language 언어 코드 (예: "en")
     * @param topic 뉴스 주제 (예: "politics")
     */
    fun fetchNews(language: String, topic: String = _selectedCategory.value) {
        _currentLanguage.value = language
        _selectedCategory.value = topic
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = GNewsApiService.api.getTopHeadlines(
                    lang = language,
                    topic = topic,
                    token = GNewsApiService.apiKey
                )

                if (response.articles.isNotEmpty()) {
                    val originalArticles = response.articles.take(7)

                    _articles.value = originalArticles

                    originalArticles.forEach { article ->
                        launch { analyzeDifficulty(article) }
                    }
                } else {
                    Log.d("NewsViewModel", "뉴스 데이터 없음")
                }

            } catch (e: Exception) {
                Log.e("NewsViewModel", "뉴스 불러오기 실패: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 현재 선택된 카테고리를 외부에서 업데이트
     */
    fun setCategory(newCategory: String) {
        _selectedCategory.value = newCategory
    }

    /**
     * 개별 뉴스 기사에 대해 본문 추출 + NLP 분석 수행
     * @param article 분석할 뉴스 기사
     * @return 난이도 분석 결과가 반영된 GNewsArticle
     */
    suspend fun analyzeDifficulty(article: GNewsArticle): GNewsArticle {
        difficultyCache[article.url]?.let {
            updateArticleDifficulty(article.url, it)
            return article.copy(difficulty = it)
        }

        return try {
            val extractResponse: Response<ExtractResponse> =
                RetrofitClient.extractService.extractArticle(mapOf("url" to article.url))

            val extractedText = extractResponse.body()?.text

            if (!extractResponse.isSuccessful || extractedText.isNullOrBlank()) {
                updateArticleDifficulty(article.url, "분석불가")
                return article.copy(difficulty = "분석불가")
            }

            Log.d("ARTICLE_BODY", "본문 길이: ${extractedText.length}")
            Log.d("ARTICLE_BODY", "본문 앞부분: ${extractedText.take(300)}")

            val nlpResponse: Response<NlpResponse> =
                RetrofitClient.nlpService.analyzeText(NlpRequest(extractedText))

            val difficulty = if (nlpResponse.isSuccessful) {
                nlpResponse.body()?.difficulty ?: "분석불가"
            } else {
                "분석불가"
            }

            difficultyCache[article.url] = difficulty
            updateArticleDifficulty(article.url, difficulty)

            article.copy(difficulty = difficulty)

        } catch (e: Exception) {
            Log.e("NLP_ANALYZE", "❌ 예외 발생: ${e.message}")
            updateArticleDifficulty(article.url, "에러")
            article.copy(difficulty = "에러")
        }
    }

    /**
     * 특정 URL에 해당하는 뉴스 카드의 난이도 필드를 갱신
     */
    private fun updateArticleDifficulty(url: String, difficulty: String) {
        val current = _articles.value.toMutableList()
        val index = current.indexOfFirst { it.url == url }

        if (index != -1) {
            current[index] = current[index].copy(difficulty = difficulty)
            _articles.value = current
        }
    }
}

