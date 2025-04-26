package com.example.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.api.NlpRequest
import com.example.capstone.data.api.NlpResponse
import com.example.capstone.data.api.RetrofitClient
import com.example.capstone.data.api.RetrofitInstance
import com.example.capstone.data.model.GNewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.sample.BuildConfig

class NewsViewModel : ViewModel() {

    private val _articles = MutableStateFlow<List<GNewsArticle>>(emptyList())
    val articles: StateFlow<List<GNewsArticle>> = _articles

    fun fetchNews(language: String, topic: String = "politics") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getTopHeadlines(
                    lang = language,
                    topic = topic,
                    token = BuildConfig.GNEWS_API_KEY
                )

                val originalArticles = response.articles.take(5)
                val enrichedArticles = originalArticles.map { article ->
                    async { analyzeDifficulty(article) }
                }.awaitAll()

                _articles.value = enrichedArticles

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("❌ NewsViewModel", "에러 발생: ${e.message}")
            }
        }
    }

    private suspend fun analyzeDifficulty(article: GNewsArticle): GNewsArticle {
        Log.d("NLP_ANALYZE", "분석할 URL: ${article.url}")

        val call = RetrofitClient.nlpService.analyzeText(NlpRequest(article.url))
        val response: Response<NlpResponse> = try {
            call.execute()
        } catch (e: Exception) {
            e.printStackTrace()
            return article.copy(difficulty = "분석불가")
        }

        return if (response.isSuccessful) {
            val difficultyFromServer = response.body()?.difficulty ?: "분석불가"
            Log.d("NLP_ANALYZE", "서버에서 받은 난이도: $difficultyFromServer")
            article.copy(difficulty = difficultyFromServer)
        } else {
            Log.e("NLP_ANALYZE", "서버 응답 실패: ${response.errorBody()?.string()}")
            article.copy(difficulty = "분석불가")
        }
    }
}

