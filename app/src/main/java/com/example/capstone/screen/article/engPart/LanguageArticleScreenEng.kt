package com.example.capstone.screen.article.engPart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capstone.viewmodel.SharedUrlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageArticleScreenEng(
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "English News") }
            )
        }
    ) { paddingValues ->
        NewsListScreen(
            modifier = Modifier.padding(paddingValues),
            language = "en",
            navController = navController,
            sharedUrlViewModel = sharedUrlViewModel
        )
    }
}





