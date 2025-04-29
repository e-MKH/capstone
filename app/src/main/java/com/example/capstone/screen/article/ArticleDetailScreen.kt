package com.example.capstone.screen.article

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.viewmodel.WordViewModel
import com.example.capstone.data.api.RetrofitTranslateClient
import com.example.capstone.data.api.TranslateRequest
import kotlinx.coroutines.launch
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ArticleDetailScreen(
    navController: NavHostController,
    url: String,
    sharedTextViewModel: SharedTextViewModel,
    wordViewModel: WordViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val textState by sharedTextViewModel.text.collectAsState()
    val titleState by sharedTextViewModel.title.collectAsState()
    var translatedText by remember { mutableStateOf<String?>(null) }
    var translatedTitle by remember { mutableStateOf<String?>(null) }
    var isTranslated by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val textResponse = RetrofitTranslateClient.translateService
                                    .translateText(TranslateRequest(textState))
                                val titleResponse = RetrofitTranslateClient.translateService
                                    .translateText(TranslateRequest(titleState))

                                if (textResponse.isSuccessful && titleResponse.isSuccessful) {
                                    translatedText = textResponse.body()?.translated_text ?: "번역 실패"
                                    translatedTitle = titleResponse.body()?.translated_text ?: "번역 실패"
                                    isTranslated = true
                                } else {
                                    translatedText = "번역 실패: 서버 오류"
                                    translatedTitle = "번역 실패: 서버 오류"
                                }
                            } catch (e: Exception) {
                                translatedText = "번역 실패: ${e.message}"
                                translatedTitle = "번역 실패: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4169E1),
                        contentColor = Color.White
                    ),
                    enabled = !isTranslated && !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("번역 요청")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        isTranslated = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4169E1),
                        contentColor = Color.White
                    ),
                    enabled = isTranslated && !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("원문 보기")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isTranslated) (translatedTitle ?: "번역 실패") else titleState,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                val words = (if (isTranslated) (translatedText ?: "번역 실패") else textState).split(" ")

                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    words.forEach { word ->
                        Text(
                            text = "$word ",
                            modifier = Modifier
                                .padding(end = 4.dp, bottom = 4.dp)
                                .clickable {
                                    val cleanWord = word.trim().replace("[^A-Za-z0-9]".toRegex(), "")
                                    if (cleanWord.isNotBlank()) {
                                        coroutineScope.launch {
                                            try {
                                                val response = RetrofitTranslateClient.translateService
                                                    .translateText(TranslateRequest(cleanWord))
                                                if (response.isSuccessful) {
                                                    val translatedWord = response.body()?.translated_text ?: "번역 실패"
                                                    wordViewModel.saveWord(cleanWord, translatedWord)
                                                    Toast.makeText(context, "'$cleanWord' 저장됨", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "번역 실패", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "에러: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
