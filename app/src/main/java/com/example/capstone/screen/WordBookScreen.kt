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
import com.example.capstone.data.local.WordEntity

/**
 * ✅ [WordBookScreen]
 * 저장된 단어들을 리스트로 보여주는 단어장 화면입니다.
 * - Room DB에서 단어 목록을 불러옴
 * - 각 단어 옆에 삭제 버튼을 제공하여 단어 제거 가능
 */
@Composable
fun WordBookScreen(
    modifier: Modifier = Modifier,
    wordViewModel: WordViewModel = viewModel()
) {
    // ✅ 단어 리스트 상태를 기억함
    var wordList by remember { mutableStateOf(listOf<WordEntity>()) }

    // ✅ 컴포저블 진입 시 단어 전체 불러오기
    LaunchedEffect(Unit) {
        wordList = wordViewModel.getAllWords()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📘 내 단어장", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

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
                        // ✅ 단어 표시
                        Text(
                            text = word.word,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // ✅ 삭제 버튼 클릭 시 단어 삭제 및 리스트 갱신
                        IconButton(onClick = {
                            wordViewModel.deleteWord(word)
                            wordList = wordList.filter { it.word != word.word }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제")
                        }
                    }
                }
            }
        }
    }
}
