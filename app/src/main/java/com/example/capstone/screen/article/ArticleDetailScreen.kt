package com.example.capstone.screen.article

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.data.api.RetrofitTranslateClient
import com.example.capstone.data.api.service.TranslateRequest
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedClickableWord(
    word: String,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }

    val backgroundColor = when {
        isPressed -> Color(0xFFB3D9FF)
        isHighlighted -> Color(0xFFFFF59D)
        else -> Color.Transparent
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 150)
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale = 1.2f
            delay(150)
            scale = 1f
            delay(200)
            isPressed = false
        }
    }

    Text(
        text = "$word ",
        modifier = Modifier
            .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clickable {
                isPressed = true
                onClick()
            },
        style = MaterialTheme.typography.bodyMedium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    navController: NavHostController,
    sharedTextViewModel: SharedTextViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val textState by sharedTextViewModel.text.collectAsState()
    val titleState by sharedTextViewModel.title.collectAsState()

    var translatedText by remember { mutableStateOf<String?>(null) }
    var translatedTitle by remember { mutableStateOf<String?>(null) }
    var isTranslated by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val highlightedSentences = remember { mutableStateListOf<Int>() }
    val translatedSentences = remember { mutableStateMapOf<Int, String>() }
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val originalText = textState
        .removePrefix(titleState)
        .replace("\n", " ")
        .replace(Regex("[^\\x00-\\x7F]+"), "") // ASCII 외 문자 제거
        .replace(Regex("\\s+"), " ")
        .trim()

    val displayedText = if (isTranslated) translatedText ?: "번역 실패" else originalText
    val displayedTitle = if (isTranslated) translatedTitle ?: "번역 실패" else titleState
    val sentenceList = displayedText.split(Regex("(?<=[.!?])\\s+"))

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
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
                                    Toast.makeText(context, "번역 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "번역 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isTranslated && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4169E1))
                ) {
                    Text("번역 요청", color = Color.White)
                }

                Button(
                    onClick = { isTranslated = false },
                    enabled = isTranslated && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4169E1))
                ) {
                    Text("원문 보기", color = Color.White)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            highlightedSentences.forEach { index ->
                                if (translatedSentences[index] == null) {
                                    val response = RetrofitTranslateClient.translateService
                                        .translateText(TranslateRequest(sentenceList[index]))
                                    if (response.isSuccessful) {
                                        translatedSentences[index] = response.body()?.translated_text ?: "번역 실패"
                                    }
                                }
                            }
                            showBottomSheet.value = true
                        }
                    },
                    enabled = highlightedSentences.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("하이라이트 번역", color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = displayedTitle,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column {
                    sentenceList.forEachIndexed { sentenceIndex, sentence ->
                        val isHighlighted = highlightedSentences.contains(sentenceIndex)

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isHighlighted) highlightedSentences.remove(sentenceIndex)
                                    else highlightedSentences.add(sentenceIndex)
                                }
                        ) {
                            sentence.trim().split(" ").forEach { word ->
                                AnimatedClickableWord(
                                    word = word,
                                    isHighlighted = isHighlighted
                                ) {
                                    coroutineScope.launch {
                                        val response = RetrofitTranslateClient.translateService
                                            .translateText(TranslateRequest(word))
                                        if (response.isSuccessful) {
                                            val translated = response.body()?.translated_text
                                            Toast.makeText(
                                                context,
                                                "'$word' → '$translated'",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp)) // 문장 간 시각적 간격
                    }
                }
            }

            if (showBottomSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet.value = false },
                    sheetState = sheetState
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        highlightedSentences.forEach { index ->
                            val original = sentenceList[index]
                            val translated = translatedSentences[index] ?: "번역 준비 중..."
                            Text("• $original", style = MaterialTheme.typography.bodySmall)
                            Text("→ $translated", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}