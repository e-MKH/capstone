package com.example.capstone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capstone.screen.article.ArticleScreen
import com.example.capstone.screen.article.ArticleDetailScreen
import com.example.capstone.screen.Settings
import com.example.capstone.screen.Info
import com.example.capstone.screen.WordBookScreen
import com.example.capstone.screen.article.engPart.LanguageArticleScreenEng
import com.example.capstone.viewmodel.SharedUrlViewModel
import com.example.capstone.viewmodel.SharedTextViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import android.util.Log
import com.example.capstone.ui.screen.LanguageArticleScreenJap
/**
 * [NavGraph]
 * 앱 전체의 Navigation 구조를 정의
 * 각 화면(Composable)을 특정 route와 매핑
 *
 * @param navController        네비게이션을 제어하는 컨트롤러
 * @param sharedUrlViewModel   기사 URL을 화면 간 공유하기 위한 ViewModel
 * @param sharedTextViewModel  기사 본문 텍스트를 공유하기 위한 ViewModel
 * @param modifier             NavHost에 적용할 Modifier (기본값 있음)
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    modifier: Modifier = Modifier
) {
    // NavHost: 시작 화면을 설정하고, 각 route에 해당하는 Composable 등록
    NavHost(
        navController = navController,
        startDestination = Screen.Article.route, // 앱 시작 시 Article 화면부터 시작
        modifier = modifier
    ) {
        // 언어 선택 화면
        composable(Screen.Article.route) {
            ArticleScreen(navController)
        }

        // 단어장 화면
        composable(Screen.WordBook.route) {
            WordBookScreen()
        }

        // 설정 화면
        composable(Screen.Settings.route) {
            Settings()
        }

        // 정보 화면
        composable(Screen.Info.route) {
            Info()
        }

        // 영어 뉴스 리스트 화면
        composable(Screen.EnglishNews.route) {
            LanguageArticleScreenEng(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel,
                sharedTextViewModel = sharedTextViewModel
            )
        }

        // 기사 상세 화면 (URL 기반 WebView)
        composable(Screen.ArticleDetail.route) {
            val url = sharedUrlViewModel.url.collectAsState().value // 공유 ViewModel에서 URL 가져오기
            Log.d("DETAIL_SCREEN", "받은 URL: $url")

            ArticleDetailScreen(
                navController = navController,
                url = url,
                sharedTextViewModel = sharedTextViewModel
            )
        }
        // 일본어 뉴스 리스트 화면
        composable(Screen.JapaneseNews.route) {  
            LanguageArticleScreenJap()
        }
    }
}
