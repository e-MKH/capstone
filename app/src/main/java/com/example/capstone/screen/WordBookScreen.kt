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


@Composable
fun WordBookScreen(
    modifier: Modifier = Modifier,
    wordViewModel: WordViewModel = viewModel()
) {

    var wordList by remember { mutableStateOf(listOf<WordEntity>()) }


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

                        Column {
                            Text(
                                text = word.word,
                                style = MaterialTheme.typography.titleMedium
                            )
                            word.meaning?.let { meaning ->
                                Text(
                                    text = meaning,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }


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
