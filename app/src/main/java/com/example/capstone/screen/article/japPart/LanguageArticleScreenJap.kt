package com.example.capstone.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capstone.viewmodel.JapaneseNewsViewModel
import com.example.capstone.viewmodel.JapaneseNewsUiState
import com.example.capstone.data.api.ArticleResult


/**
 * [JapaneseNewsScreen]
 * - 일본어 뉴스 기사 + 난이도 분석 결과를 보여주는 화면
 */
@Composable
fun LanguageArticleScreenJap(
    viewModel: JapaneseNewsViewModel = viewModel()
) {
    // ViewModel에서 상태 가져오기 (Compose에서 observe)
    val state by viewModel.uiState.collectAsState()

    // 전체 화면 구성
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "🇯🇵 일본어 뉴스 난이도 분석",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 로딩 중 표시
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        // 에러 발생 시 메시지 표시
        state.error?.let { errorMsg ->
            Text("⚠ 오류: $errorMsg", color = MaterialTheme.colorScheme.error)
        }

        // 기사 리스트 표시
        LazyColumn {
            items(state.articles) { article: ArticleResult ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = article.original,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("난이도: ${article.analysis.level}")
                        Text("점수: ${article.analysis.score}")
                    }
                }
            }
        }

    }
}
