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
import com.example.capstone.viewmodel.JaNewsViewModel
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryNewsScreen(
    language: String,
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val jaViewModel: JaNewsViewModel = viewModel()
    val enViewModel: NewsViewModel = viewModel()

    val isJapanese = language == "ja"
    val viewModel = if (isJapanese) jaViewModel else enViewModel

    val articles by if (isJapanese)
        jaViewModel.primaryArticles.collectAsState()
    else
        enViewModel.primaryArticles.collectAsState()

    val selectedCategory by if (isJapanese)
        jaViewModel.selectedCategory.collectAsState()
    else
        enViewModel.selectedCategory.collectAsState()

    val isLoading by if (isJapanese)
        jaViewModel.isLoading.collectAsState()
    else
        enViewModel.isLoading.collectAsState()

    val categories = listOf(
        "정치" to "politics",
        "경제" to "business",
        "사회" to "world",
        "기술" to "technology",
        "과학" to "science",
        "연예" to "entertainment",
        "건강" to "health",
        "스포츠" to "sports"
    )

    val currentLabel = categories.firstOrNull { it.second == selectedCategory }?.first ?: "정치"
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(language, selectedCategory) {
        if (articles.isEmpty()) {
            if (isJapanese)
                jaViewModel.fetchJapaneseNews(selectedCategory)
            else
                enViewModel.fetchNews(language, selectedCategory)
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = currentLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("카테고리 선택") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { (label, query) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    expanded = false
                                    if (isJapanese) {
                                        jaViewModel.setCategory(query)
                                        jaViewModel.fetchJapaneseNews(query)
                                    } else {
                                        enViewModel.setCategory(query)
                                        enViewModel.fetchNews(language, query, forceRefresh = true)
                                    }
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        if (isJapanese)
                            jaViewModel.fetchJapaneseNews(selectedCategory)
                        else
                            enViewModel.fetchNews(language, selectedCategory, forceRefresh = true)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (isLoading && articles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(articles) { article ->
                        ArticleCard(article = article) {
                            coroutineScope.launch {
                                try {
                                    sharedUrlViewModel.setUrl(article.url)
                                    val response = RetrofitClient.extractService.extractArticle(mapOf("url" to article.url))
                                    if (response.isSuccessful) {
                                        val text = response.body()?.text ?: ""
                                        sharedTextViewModel.setText(
                                            newText = text,
                                            lang = language,
                                            newTitle = article.title
                                        )
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
    }
}