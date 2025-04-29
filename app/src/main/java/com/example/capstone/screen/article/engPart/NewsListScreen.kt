package com.example.capstone.screen.article.engPart

import android.app.Application
import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

fun formatDate(isoTime: String): String {
    return try {
        if (isoTime.length >= 16) {
            isoTime.substring(0, 16).replace("T", " ")
        } else {
            isoTime
        }
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

    val wordViewModel: WordViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing)

    val categories = listOf(
        "정치" to "politics",
        "경제" to "business",
        "사회" to "world",
        "기술" to "technology",
        "과학" to "science"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
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
                                if (!url.isNullOrBlank() && (url.startsWith("http://") || url.startsWith("https://"))) {
                                    sharedUrlViewModel.setUrl(url)

                                    coroutineScope.launch {
                                        try {
                                            val response = RetrofitClient.extractService.extractArticle(
                                                mapOf("url" to url)
                                            )
                                            if (response.isSuccessful) {
                                                val body = response.body()
                                                sharedTextViewModel.setText(body?.text ?: "")
                                                sharedTextViewModel.setTitle(article.title)
                                                navController.navigate("detail")
                                            } else {
                                                Toast.makeText(context, "본문 추출 실패", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "본문 추출 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "잘못된 기사 URL입니다.", Toast.LENGTH_SHORT).show()
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
                                article.difficulty?.let { level ->
                                    Text(
                                        text = level,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = when (level) {
                                            "고급" -> MaterialTheme.colorScheme.error
                                            "중급" -> MaterialTheme.colorScheme.primary
                                            "초급" -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.outline
                                        },
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatDate(article.publishedAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            article.description?.let { description ->
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
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





