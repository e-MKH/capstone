package com.example.capstone.screen.article.engPart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.capstone.viewmodel.NytNewsViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.data.model.NytArticle
import com.example.capstone.navigation.Screen
import com.example.capstone.data.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertListScreen(
    navController: NavHostController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    viewModel: NytNewsViewModel = viewModel()
) {
    val articles by viewModel.articles.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val levels = listOf("입문", "초급~고급", "전문가")
    var selectedLevel by remember { mutableStateOf("전문가") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchExpertArticles()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("전문가용 뉴스") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(0.5f)
            ) {
                TextField(
                    value = selectedLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("난이도 선택") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    levels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level) },
                            onClick = {
                                selectedLevel = level
                                expanded = false
                                when (level) {
                                    "입문" -> navController.navigate(Screen.EasyNews.route)
                                    "초급~고급" -> navController.navigate(Screen.EnglishNews.route)
                                    "전문가" -> {}
                                }
                            }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(articles) { article ->
                    ExpertNewsCard(
                        article = article,
                        onClick = {
                            sharedUrlViewModel.setUrl(article.url)
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.extractService.extractArticle(mapOf("url" to article.url))
                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        sharedTextViewModel.setText(body?.text ?: "", sourceUrl = article.url)
                                        sharedTextViewModel.setTitle(article.title)
                                        navController.navigate(Screen.ArticleDetail.route)
                                    } else {
                                        Toast.makeText(
                                            navController.context,
                                            "본문 추출 실패",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        navController.context,
                                        "본문 추출 실패: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpertNewsCard(
    article: NytArticle,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
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
                    text = article.difficulty ?: "전문가",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.abstract ?: "요약 없음",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


