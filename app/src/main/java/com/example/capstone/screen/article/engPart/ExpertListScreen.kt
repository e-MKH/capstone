package com.example.capstone.screen.article.engPart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertListScreen(
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expert News") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ✅ 상단 난이도 선택 버튼
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    navController.navigate("easy_list")
                }) {
                    Text("입문자용")
                }

                Button(onClick = {
                    navController.navigate("eng") // 초~고급 뉴스
                }) {
                    Text("초급~고급")
                }

                Button(onClick = { /* 현재 위치 */ }) {
                    Text("전문가용")
                }
            }

            // ✅ 전문가 뉴스 화면 내용
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(text = "전문가용 뉴스 화면 (준비 중)", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}