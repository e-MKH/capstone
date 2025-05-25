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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBookScreen(
    modifier: Modifier = Modifier,
    wordViewModel: WordViewModel = viewModel()
) {
    var wordList by remember { mutableStateOf(listOf<Vocabulary>()) }
    var sortAlphabetically by remember { mutableStateOf(false) }

    // 정렬 변경 시 리스트 업데이트
    LaunchedEffect(sortAlphabetically) {
        val list = wordViewModel.getAllWords()
        wordList = if (sortAlphabetically) {
            list.sortedBy { it.word.lowercase() }
        } else {
            list
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📘 내 단어장", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 정렬 스위치
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("알파벳순 정렬", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = sortAlphabetically,
                onCheckedChange = { sortAlphabetically = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                            Text(word.word, style = MaterialTheme.typography.titleMedium)
                            Text(
                                word.meaning,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        IconButton(onClick = {
                            wordViewModel.deleteWord(word)
                            wordList = wordList.filter { it.vocaId != word.vocaId }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제")
                        }
                    }
                }
            }
        }
    }
}