package com.example.capstone.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageArticleScreen(language: String, parentNavController: NavHostController) {
    val tabNavController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {}, // 👉 제목 없애기 (빈 상태)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = tabNavController)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 콘텐츠: 하단탭 네비게이션
            NavHost(
                navController = tabNavController,
                startDestination = "politics",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("politics") { politics() }
                composable("social") { social() }
                composable("economy") { economy() }
            }

            // 오른쪽 하단에 뒤로가기 버튼
            IconButton(
                onClick = { parentNavController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    }
}



