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
    var rawWordList by remember { mutableStateOf(listOf<Vocabulary>()) }
    var wordList by remember { mutableStateOf(listOf<Vocabulary>()) }

    val sortOptions = listOf("최신순", "알파벳순")
    var selectedSort by remember { mutableStateOf("최신순") }
    var expanded by remember { mutableStateOf(false) }

    // 정렬 함수
    fun applySorting() {
        wordList = when (selectedSort) {
            "알파벳순" -> rawWordList.sortedBy { it.word.lowercase() }
            else -> rawWordList.sortedByDescending { it.vocaId }
        }
    }

    // 최초 로딩
    LaunchedEffect(Unit) {
        rawWordList = wordViewModel.getAllWords()
        applySorting()
    }

    // 정렬 옵션 변경 시 정렬 적용
    LaunchedEffect(selectedSort) {
        applySorting()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📘 내 단어장", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 정렬 드롭다운
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedSort,
                onValueChange = {},
                readOnly = true,
                label = { Text("정렬 방식") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sortOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedSort = option
                            expanded = false
                        }
                    )
                }
            }
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
                            rawWordList = rawWordList.filter { it.vocaId != word.vocaId }
                            applySorting()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제")
                        }
                    }
                }
            }
        }
    }
}
