package com.example.capstone.screen.article.engPart

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capstone.viewmodel.NewsViewModel
import com.example.capstone.viewmodel.WordViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.data.api.RetrofitClient
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

/**
 * ISO 8601 형식 날짜를 간단하게 변환해주는 함수
 */
fun formatDate(isoTime: String): String {
    return try {
        if (isoTime.length >= 16) {
            isoTime.substring(0, 16).replace("T", " ")
        } else isoTime
    } catch (e: Exception) {
        isoTime
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    modifier: Modifier = Modifier,
    language: String, // 언어 코드 ("en", "ja", "zh")
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    viewModel: NewsViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val articles by viewModel.articles.collectAsState() // 뉴스 목록
    val isLoading by viewModel.isLoading.collectAsState() // 로딩 상태

    // 단어장 ViewModel (Room DB 사용)
    val wordViewModel: WordViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing)
    val listState = rememberLazyListState()

    // 카테고리 목록 및 드롭다운 상태
    val categories = listOf(
        "정치" to "politics", "경제" to "business", "사회" to "world",
        "기술" to "technology", "과학" to "science"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {

        // 카테고리 드롭다운 메뉴
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(12.dp)
        ) {
            TextField(
                value = selectedCategory.first,
                onValueChange = {},
                readOnly = true,
                label = { Text("카테고리 선택") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { (label, query) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedCategory = label to query
                            expanded = false
                            viewModel.fetchNews(language, query)
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        }
                    )
                }
            }
        }

        // Swipe-to-Refresh 기능
        SwipeRefresh(
            state = refreshState,
            onRefresh = {
                isRefreshing = true
                viewModel.fetchNews(language, selectedCategory.second)
                isRefreshing = false
            },
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading && articles.isEmpty()) {
                // 로딩 중일 때 로딩 인디케이터 표시
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // 뉴스 기사 리스트 표시
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(articles) { article ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    val url = article.url
                                    if (!url.isNullOrBlank()) {
                                        sharedUrlViewModel.setUrl(url)
                                        coroutineScope.launch {
                                            try {
                                                // Flask 서버에서 본문 추출 요청
                                                val response = RetrofitClient.extractService.extractArticle(mapOf("url" to url))
                                                if (response.isSuccessful) {
                                                    val body = response.body()
                                                    sharedTextViewModel.setText(body?.text ?: "")
                                                    sharedTextViewModel.setTitle(article.title)
                                                    navController.navigate("detail") // 상세화면으로 이동
                                                } else {
                                                    Toast.makeText(context, "본문 추출 실패", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "본문 추출 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // ✅ 제목 + 난이도 표시
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = article.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = when (val level = article.difficulty) {
                                            null -> "분석중..."
                                            "분석불가", "에러" -> "분석불가"
                                            else -> level
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = when (article.difficulty) {
                                            "고급" -> MaterialTheme.colorScheme.error
                                            "중급" -> MaterialTheme.colorScheme.primary
                                            "초급" -> MaterialTheme.colorScheme.secondary
                                            "분석불가", "에러" -> Color.Gray
                                            else -> Color.Gray
                                        },
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // 게시일 표시
                                Text(
                                    text = formatDate(article.publishedAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // 기사 요약(설명)
                                article.description?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }

                        // 난이도 미분석된 기사만 백그라운드 분석
                        LaunchedEffect(article.url) {
                            if (article.difficulty == null) {
                                coroutineScope.launch {
                                    viewModel.analyzeDifficulty(article)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 초기 진입 시 뉴스 데이터 로딩
    LaunchedEffect(language) {
        viewModel.fetchNews(language, selectedCategory.second)
    }
}


