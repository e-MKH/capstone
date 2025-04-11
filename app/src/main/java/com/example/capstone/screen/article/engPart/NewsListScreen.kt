package com.example.capstone.screen.article.engPart

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capstone.viewmodel.NewsViewModel
import com.example.capstone.viewmodel.WordViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * ✅ [NewsListScreen]
 * GNews API를 통해 기사를 불러오고, 단어를 클릭하면 단어장에 저장되는 UI입니다.
 * - 카테고리 드롭다운으로 기사 주제 선택
 * - 단어 클릭 시 Room DB에 저장
 * - 저장 성공 시 Toast로 사용자에게 알림
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    modifier: Modifier = Modifier,
    language: String,
    viewModel: NewsViewModel = viewModel()
) {
    val context = LocalContext.current

    // ✅ API에서 받아온 기사 목록 상태 관찰
    val articles by viewModel.articles.collectAsState()

    // ✅ 단어장 저장 ViewModel 생성
    val wordViewModel: WordViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing)

    // ✅ 사용 가능한 기사 카테고리 목록
    val categories = listOf(
        "정치" to "politics",
        "경제" to "business",
        "사회" to "world",
        "기술" to "technology",
        "과학" to "science"
    )

    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {

        // ✅ 드롭다운으로 카테고리 선택
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
                        }
                    )
                }
            }
        }

        // ✅ 뉴스 기사 목록
        SwipeRefresh(
            state = refreshState,
            onRefresh = {
                isRefreshing = true
                viewModel.fetchNews(language, selectedCategory.second)
                isRefreshing = false
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(articles) { article ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            article.description?.let { description ->
                                // ✅ 기사 본문 텍스트를 단어 단위로 나눠서 클릭 가능하게 표시
                                ClickableWordText(description) { clickedWord ->
                                    wordViewModel.saveWord(clickedWord)
                                    Toast.makeText(context, "'$clickedWord' 저장됨", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ✅ 화면 진입 시 뉴스 자동 로딩
    LaunchedEffect(language) {
        viewModel.fetchNews(language, selectedCategory.second)
    }
}

/**
 * ✅ [ClickableWordText]
 * 기사 내용을 띄어쓰기로 단어를 나누고, 각 단어를 클릭할 수 있도록 렌더링합니다.
 * 클릭 시 전달된 콜백(onWordClick)을 실행합니다.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClickableWordText(
    content: String,
    onWordClick: (String) -> Unit
) {
    val words = content.split(" ")

    FlowRow(modifier = Modifier.fillMaxWidth()) {
        words.forEach { word ->
            Text(
                text = "$word ",
                modifier = Modifier
                    .padding(end = 4.dp, bottom = 4.dp)
                    .clickable {
                        val cleanWord = word.trim().replace("[^A-Za-z0-9]".toRegex(), "")
                        onWordClick(cleanWord)
                    },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
