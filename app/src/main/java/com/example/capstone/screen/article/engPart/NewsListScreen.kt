package com.example.capstone.screen.article.engPart

import android.app.Application
import android.util.Log
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
    language: String,
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    viewModel: NewsViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val articles by viewModel.articles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val wordViewModel: WordViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing)
    val listState = rememberLazyListState()

    val levels = listOf("초급~고급", "입문", "전문가")
    var selectedLevel by remember { mutableStateOf(levels[0]) }
    var levelExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "정치" to "politics", "경제" to "business", "사회" to "world",
        "기술" to "technology", "과학" to "science"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = levelExpanded,
                onExpandedChange = { levelExpanded = !levelExpanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = selectedLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("난이도 선택") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(levelExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = levelExpanded,
                    onDismissRequest = { levelExpanded = false }
                ) {
                    levels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level) },
                            onClick = {
                                selectedLevel = level
                                levelExpanded = false
                                when (level) {
                                    "입문" -> navController.navigate("easy")
                                    "전문가" -> navController.navigate("expert")
                                    "초급~고급" -> viewModel.fetchNews(language, selectedCategory.second)
                                }
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = selectedCategory.first,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("카테고리 선택") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { (label, query) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedCategory = label to query
                                categoryExpanded = false
                                viewModel.fetchNews(language, query)
                                coroutineScope.launch {
                                    listState.scrollToItem(0)
                                }
                            }
                        )
                    }
                }
            }
        }

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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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
                                                val response = RetrofitClient.extractService.extractArticle(mapOf("url" to url))
                                                if (response.isSuccessful) {
                                                    val body = response.body()
                                                    val text = body?.text ?: ""

                                                    Log.d("ARTICLE_BODY", "본문 길이: ${text.length}")
                                                    Log.d("ARTICLE_BODY", "본문 앞부분: ${text.take(300)}")

                                                    sharedTextViewModel.setText(text)
                                                    sharedTextViewModel.setTitle(article.title)
                                                    navController.navigate("detail")
                                                }
                                                else {
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
                                            in listOf("분석불가", "에러") -> Color.Gray
                                            else -> Color.Gray
                                        },
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = formatDate(article.publishedAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

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

    LaunchedEffect(language) {
        viewModel.fetchNews(language, selectedCategory.second)
    }
}

