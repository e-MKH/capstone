package com.example.capstone.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capstone.viewmodel.WordViewModel
import com.example.capstone.data.local.entity.Vocabulary

/**
 * [WordBookScreen]
 * 사용자가 저장한 단어들을 목록으로 보여주는 화면입니다.
 * - 단어 클릭 시 아무 동작은 없고,
 * - 우측 쓰레기통 버튼으로 삭제 가능
 *
 * @param modifier 화면 전체 레이아웃에 적용할 Modifier
 * @param wordViewModel Room DB 연동을 위한 ViewModel
 */
@Composable
fun WordBookScreen(
    modifier: Modifier = Modifier,
    wordViewModel: WordViewModel = viewModel()
) {
    // 단어장에 저장된 단어 리스트 상태
    var wordList by remember { mutableStateOf(listOf<Vocabulary>()) }

    // 화면 진입 시 단어 불러오기 (1회 실행)
//    LaunchedEffect(Unit) {
//        wordList = wordViewModel.getAllWords()
//    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 화면 상단 제목
        Text("📘 내 단어장", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 단어 리스트 표시
        LazyColumn {
            items(wordList) { word ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            // 단어 본문
                            Text(
                                text = word.word,
                                style = MaterialTheme.typography.titleMedium
                            )

                            // 단어 뜻이 있다면 함께 표시
                            word.meaning?.let { meaning ->
                                Text(
                                    text = meaning,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        // 삭제 버튼
                        IconButton(onClick = {
                            wordViewModel.deleteWord(word) // DB에서 삭제
                            wordList = wordList.filter { it.word != word.word } // UI에서도 제거
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제")
                        }
                    }
                }
            }
        }
    }
}
