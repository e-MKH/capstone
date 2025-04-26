package com.example.capstone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.capstone.screen.article.ArticleScreen
import com.example.capstone.screen.article.ArticleDetailScreen
import com.example.capstone.screen.Settings
import com.example.capstone.screen.Info
import com.example.capstone.screen.WordBookScreen
import com.example.capstone.screen.article.engPart.LanguageArticleScreenEng
import com.example.capstone.viewmodel.SharedUrlViewModel
import com.example.capstone.data.api.NlpEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier


@Composable
fun NavGraph(
    navController: NavHostController,
    sharedUrlViewModel: SharedUrlViewModel,
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
        composable(Screen.EnglishNews.route) {

            LanguageArticleScreenEng(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel
            )
        }

        composable(Screen.ArticleDetail.route) {

            val url = sharedUrlViewModel.url.collectAsState().value
            Log.d("DETAIL_SCREEN", "받은 URL: $url")
            ArticleDetailScreen(navController = navController, url = url)
        }
    }
}
//
