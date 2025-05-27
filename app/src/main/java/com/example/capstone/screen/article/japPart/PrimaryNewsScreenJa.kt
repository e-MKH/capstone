package com.example.capstone.screen.article.jaPart

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
import com.example.capstone.viewmodel.JaNewsViewModel
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryNewsScreenJa(
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    viewModel: JaNewsViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val articles by viewModel.primaryArticles.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val categories = listOf(
        "정치" to "政治",
        "경제" to "経済",
        "사회" to "国際",
        "기술" to "技術",
        "과학" to "科学",
        "엔터테인먼트" to "エンタメ",
        "건강" to "健康",
        "스포츠" to "スポーツ"
    )

    val currentLabel = categories.firstOrNull { it.second == selectedCategory }?.first ?: "정치"
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchJapaneseNews(selectedCategory, forceRefresh = true)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
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
                        categories.forEach { (label, key) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    expanded = false
                                    viewModel.setCategory(key)
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.fetchJapaneseNews(selectedCategory, forceRefresh = true)
                    }
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
                                sharedUrlViewModel.setUrl(article.url)
                                sharedTextViewModel.setText(
                                    newText = article.content ?: "",
                                    lang = "ja",
                                    newTitle = article.title
                                )
                                navController.navigate("detail")
                            }
                        }
                    }
                }
            }
        }
    }
}


