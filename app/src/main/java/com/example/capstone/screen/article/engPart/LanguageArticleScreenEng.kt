package com.example.capstone.screen.article.engPart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capstone.viewmodel.SharedUrlViewModel
import com.example.capstone.viewmodel.SharedTextViewModel

/**
 * [LanguageArticleScreenEng]
 * 영어 뉴스를 보여주는 상단 화면 구성
 * - TopAppBar와 뉴스 리스트 영역(NewsListScreen)을 포함
 * @param navController         현재 화면에서 다른 화면으로 이동하기 위한 NavController
 * @param sharedUrlViewModel    선택한 뉴스의 URL을 공유하기 위한 ViewModel
 * @param sharedTextViewModel   선택한 뉴스의 본문 텍스트를 공유하기 위한 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageArticleScreenEng(
    navController: NavController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel
) {
    Scaffold(
        topBar = {
            // 상단 앱바 - "English News" 제목 표시
            TopAppBar(
                title = { Text(text = "English News") }
            )
        }
    ) { paddingValues ->
        // 뉴스 리스트 화면 호출
        // 실제 뉴스 목록은 NewsListScreen 내부에서 구성됨
        NewsListScreen(
            modifier = Modifier.padding(paddingValues),
            language = "en", // 영어 기사 전용
            navController = navController,
            sharedUrlViewModel = sharedUrlViewModel,
            sharedTextViewModel = sharedTextViewModel
        )
    }
}






