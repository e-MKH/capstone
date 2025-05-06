package com.example.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.model.GNewsArticle
import com.example.capstone.data.repository.EasyNewsRepository
import com.example.capstone.data.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EasyNewsViewModel : ViewModel() {

    private val repository = EasyNewsRepository(RetrofitInstance.easyNewsService)

    private val _easyArticles = MutableStateFlow<List<GNewsArticle>>(emptyList())
    val easyArticles: StateFlow<List<GNewsArticle>> = _easyArticles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchEasyNews(limit: Int = 5) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val easyNewsList = repository.fetchEasyNews().shuffled().take(limit)
                val converted = easyNewsList.map {
                    GNewsArticle(
                        title = it.title,
                        description = it.description ?: "",
                        url = it.url,
                        image = null,
                        publishedAt = it.publishedAt ?: "",
                        difficulty = "입문"
                    )
                }
                _easyArticles.value = converted
            } catch (e: Exception) {
                Log.e("EasyNewsViewModel", "쉬운 뉴스 로딩 실패: ${e.message}")
                _easyArticles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
