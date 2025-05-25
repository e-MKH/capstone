package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import com.example.capstone.data.api.ArticleResult
import com.example.capstone.data.api.JapaneseNewsResponse
import com.example.capstone.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * [JapaneseNewsUiState]
 * UI에서 사용할 데이터 상태를 담는 클래스
 * - articles: 분석된 기사 리스트
 * - isLoading: 로딩 중 여부
 * - error: 오류 메시지 (null이면 오류 없음)
 */
data class JapaneseNewsUiState(
    val articles: List<ArticleResult> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * [JapaneseNewsViewModel]
 * - 일본어 뉴스 기사 난이도 분석 데이터를 관리하는 ViewModel
 * - 서버에 요청하고 응답 결과를 상태로 저장
 */
class JapaneseNewsViewModel : ViewModel() {

    // MutableStateFlow: 내부에서 상태 변경 가능
    private val _uiState = MutableStateFlow(JapaneseNewsUiState())

    // 외부에서는 읽기 전용으로 노출 (Compose에서 observe할 때 사용)
    val uiState: StateFlow<JapaneseNewsUiState> = _uiState

    // ViewModel이 생성되면 자동으로 기본 카테고리 기사 분석 요청
    init {
        fetchJapaneseNews("science") // 초기 카테고리 (예: 과학)
    }

    /**
     * [fetchJapaneseNews]
     * NewsData.io + Flask 서버를 통해 일본어 기사 분석 요청
     * @param category - 뉴스 카테고리 ("science", "politics" 등)
     */
    fun fetchJapaneseNews(category: String) {
        // 요청 시작: 로딩 상태 true, 오류 초기화
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        // Flask 서버에 Retrofit 요청 보내기
        val call = RetrofitClient.japaneseNlpService.getAnalyzedJapaneseNews(category)
        call.enqueue(object : Callback<JapaneseNewsResponse> {

            // 응답 수신 성공
            override fun onResponse(
                call: Call<JapaneseNewsResponse>,
                response: Response<JapaneseNewsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 성공한 경우: 기사 리스트 갱신, 로딩 종료
                    _uiState.value = JapaneseNewsUiState(
                        articles = response.body()!!.results,
                        isLoading = false
                    )
                } else {
                    // 실패한 경우: 오류 메시지 표시
                    _uiState.value = JapaneseNewsUiState(
                        isLoading = false,
                        error = "응답 실패: ${response.code()}"
                    )
                }
            }

            // 네트워크 오류 또는 서버 문제
            override fun onFailure(call: Call<JapaneseNewsResponse>, t: Throwable) {
                _uiState.value = JapaneseNewsUiState(
                    isLoading = false,
                    error = t.message ?: "알 수 없는 오류 발생"
                )
            }
        })
    }
}

