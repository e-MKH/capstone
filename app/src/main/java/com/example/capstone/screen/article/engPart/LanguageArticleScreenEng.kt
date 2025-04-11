package com.example.capstone.screen.article.engPart

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

/**
 * ✅ [LanguageArticleScreenEng]
 * 영어 뉴스 기사 화면입니다.
 * GNews API를 통해 영어("en") 뉴스 데이터를 가져오고, NewsListScreen에 전달합니다.
 *
 * @param parentNavController 메인에서 전달된 내비게이션 컨트롤러
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageArticleScreenEng(parentNavController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "English News") } // ✅ 화면 상단 제목
            )
        }
    ) { padding ->

        // ✅ 뉴스 리스트 Composable 호출 (실제 UI는 NewsListScreen에서 구성됨)
        NewsListScreen(
            modifier = Modifier.padding(padding),
            language = "en" // ✅ 영어 기사 호출 (GNews API에서 "en" 전달됨)
        )
    }
}





