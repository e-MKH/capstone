package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.api.RetrofitInstance
import com.example.capstone.data.model.GNewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ✅ [NewsViewModel]
 * 뉴스 기사 데이터를 관리하는 ViewModel입니다.
 * GNews API에서 기사 데이터를 비동기적으로 가져와 화면에 전달합니다.
 */
class NewsViewModel : ViewModel() {

    // ✅ 뉴스 기사 리스트를 저장하는 상태 (Mutable → 외부에서는 읽기 전용으로 제공)
    private val _articles = MutableStateFlow<List<GNewsArticle>>(emptyList())

    // ✅ UI에서 구독할 수 있는 기사 상태값
    val articles: StateFlow<List<GNewsArticle>> = _articles

    /**
     * ✅ 뉴스 API 호출 함수
     * @param language 언어 코드 (ex. "en", "ja", "zh")
     * @param topic 기사 주제 (ex. "politics", "science")
     */
    fun fetchNews(language: String, topic: String = "politics") {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTopHeadlines(
                    lang = language,
                    topic = topic,
                    token = "45d1b5187e3a878d4ad9011073361825" // 🔑 발급받은 API 키
                )
                _articles.value = response.articles // ✅ 가져온 기사 리스트 업데이트
            } catch (e: Exception) {
                e.printStackTrace() // ✅ 네트워크 오류 등 처리
            }
        }
    }
}
