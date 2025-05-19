package com.example.capstone.screen.article.engPart

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capstone.data.api.RetrofitClient
import com.example.capstone.ui.components.ArticleCard
import com.example.capstone.viewmodel.NewsViewModel
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryNewsScreen(
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    viewModel: NewsViewModel = viewModel()
) {
    val articles by viewModel.secondaryArticles.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(selectedCategory) {
        if (articles.isEmpty()) {
            viewModel.fetchNews("en", selectedCategory)
        }
    }

    var sortOption by remember { mutableStateOf("기본") }
    val sortedArticles = when (sortOption) {
        "난이도 오름차순" -> articles.sortedBy { it.difficulty }
        "난이도 내림차순" -> articles.sortedByDescending { it.difficulty }
        else -> articles
    }

    val categories = listOf(
        "정치" to "politics",
        "경제" to "business",
        "사회" to "world",
        "기술" to "technology",
        "과학" to "science"
    )
    val currentLabel = categories.firstOrNull { it.second == selectedCategory }?.first ?: "정치"
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedSort by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 카테고리 드롭다운
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = currentLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("카테고리") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                    modifier = Modifier.menuAnchor()
                )

                DropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { (label, query) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                expandedCategory = false
                                viewModel.setCategory(query)
                                viewModel.isLoading.value = true
                                viewModel.fetchNews("en", query)
                            }
                        )
                    }
                }
            }

            // 정렬 드롭다운
            ExposedDropdownMenuBox(
                expanded = expandedSort,
                onExpandedChange = { expandedSort = !expandedSort },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = sortOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("정렬") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedSort) },
                    modifier = Modifier.menuAnchor()
                )

                DropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false }
                ) {
                    listOf("기본", "오름차순", "내림차순").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                sortOption = option
                                expandedSort = false
                            }
                        )
                    }
                }
            }

            // 새로고침
            IconButton(
                onClick = {
                    viewModel.fetchNews("en", selectedCategory, forceRefresh = true)
                }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "새로고침")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading && sortedArticles.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            !isLoading && sortedArticles.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("표시할 중급/고급 기사가 없습니다.", style = MaterialTheme.typography.bodyLarge)
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sortedArticles) { article ->
                        ArticleCard(article = article) {
                            coroutineScope.launch {
                                try {
                                    sharedUrlViewModel.setUrl(article.url)
                                    val response = RetrofitClient.extractService.extractArticle(mapOf("url" to article.url))
                                    if (response.isSuccessful) {
                                        val text = response.body()?.text ?: ""
                                        sharedTextViewModel.setText(text)
                                        sharedTextViewModel.setTitle(article.title)
                                        navController.navigate("detail")
                                    } else {
                                        Toast.makeText(context, "본문 추출 실패", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "에러: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        LaunchedEffect(articles) {
            println("🔍 SecondaryArticles 수: ${articles.size}")
            articles.forEach {
                println("▶️ ${it.title} - ${it.difficulty}")
            }
        }
    }
}