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
            val levels = listOf("입문", "초급~고급", "전문가")
            var selectedLevel by remember { mutableStateOf("전문가") }
            var expanded by remember { mutableStateOf(false) }

            // ✅ 난이도 드롭다운
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(0.5f)
            ) {
                TextField(
                    value = selectedLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("난이도 선택") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    levels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level) },
                            onClick = {
                                selectedLevel = level
                                expanded = false
                                when (level) {
                                    "입문" -> navController.navigate("easy")
                                    "초급~고급" -> navController.navigate("news")
                                    "전문가" -> {} // 현재 화면
                                }
                            }
                        )
                    }
                }
            }

            // ✅ 전문가 뉴스 화면 내용
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "전문가용 뉴스 화면 (준비 중)",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
