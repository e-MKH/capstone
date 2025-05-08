package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.api.RetrofitInstance
import com.example.capstone.data.model.NytArticle
import com.example.sample.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class NytNewsViewModel : ViewModel() {
    private val _articles = MutableStateFlow<List<NytArticle>>(emptyList())
    val articles: StateFlow<List<NytArticle>> = _articles

    fun fetchExpertArticles() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.nytApi.getExpertArticles(BuildConfig.NYT_API_KEY)

                // ✅ 잘못된 기사 제거: EmbeddedInteractive, url 없는 항목 제거
                val validArticles = response.results.filter {
                    it.item_type == "Article" && !it.url.isNullOrBlank()
                }

                _articles.value = validArticles

            } catch (e: Exception) {
                Log.e("NYT", "API 오류: ${e.message}")
            }
        }
    }
}
