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
fun PrimaryNewsScreen(
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    viewModel: NewsViewModel = viewModel()
) {
    val articles by viewModel.primaryArticles.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val categories = listOf(
        "정치" to "politics",
        "경제" to "business",
        "사회" to "world",
        "기술" to "technology",
        "과학" to "science"
    )
    val currentLabel = categories.firstOrNull { it.second == selectedCategory }?.first ?: "정치"
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCategory) {
        if (articles.isEmpty()) {
            viewModel.fetchNews("en", selectedCategory)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ✅ 카테고리 선택 + 새로고침 Row
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
                    modifier = Modifier
                        .weight(1f)
                        .menuAnchor()
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
                                viewModel.setCategory(query)
                                viewModel.isLoading.value = true
                                viewModel.fetchNews("en", query)
                            }
                        )
                    }
                }
            }

            // ✅ 수동 새로고침 버튼
            IconButton(
                onClick = {
                    viewModel.fetchNews("en", selectedCategory, forceRefresh = true)
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
}
