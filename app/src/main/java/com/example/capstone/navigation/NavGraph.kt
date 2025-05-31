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
import com.example.capstone.screen.article.engPart.PrimaryNewsScreen
import com.example.capstone.screen.article.engPart.SecondaryNewsScreen
import com.example.capstone.screen.article.jaPart.PrimaryNewsScreenJa
import com.example.capstone.screen.article.jaPart.SecondaryNewsScreenJa
//import com.example.capstone.screen.article.esPart.PrimaryNewsScreenEs
//import com.example.capstone.screen.article.esPart.SecondaryNewsScreenEs
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import android.util.Log

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedUrlViewModel: SharedUrlViewModel,
    sharedTextViewModel: SharedTextViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Article.route,
        modifier = modifier
    ) {
        composable(Screen.Article.route) {
            ArticleScreen(navController)
        }

        composable(Screen.WordBook.route) {
            WordBookScreen()
        }

        composable(Screen.Settings.route) {
            Settings()
        }

        composable(Screen.Info.route) {
            Info()
        }

        composable(Screen.ArticleDetail.route) {
            val url = sharedUrlViewModel.url.collectAsState().value
            Log.d("DETAIL_SCREEN", "받은 URL: $url")

            ArticleDetailScreen(
                navController = navController,
                sharedTextViewModel = sharedTextViewModel
            )
        }

        // 영어 뉴스 화면
        composable(Screen.PrimaryNews.route) { backStackEntry ->
            PrimaryNewsScreen(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel,
                sharedTextViewModel = sharedTextViewModel
            )
        }

        composable(Screen.SecondaryNews.route) { backStackEntry ->
            SecondaryNewsScreen(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel,
                sharedTextViewModel = sharedTextViewModel
            )
        }


        // 일본어 뉴스 화면
        composable(Screen.PrimaryNewsJa.route) {
            PrimaryNewsScreenJa(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel,
                sharedTextViewModel = sharedTextViewModel
            )
        }

        composable(Screen.SecondaryNewsJa.route) {
            SecondaryNewsScreenJa(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel,
                sharedTextViewModel = sharedTextViewModel
            )
        }

        // 스페인어 뉴스 화면
//        composable(Screen.PrimaryNewsEs.route) {
//            PrimaryNewsScreenEs(
//                navController = navController,
//                sharedUrlViewModel = sharedUrlViewModel,
//                sharedTextViewModel = sharedTextViewModel
//            )
//        }
//
//        composable(Screen.SecondaryNewsEs.route) {
//            SecondaryNewsScreenEs(
//                navController = navController,
//                sharedUrlViewModel = sharedUrlViewModel,
//                sharedTextViewModel = sharedTextViewModel
//            )
//        }
    }
}
