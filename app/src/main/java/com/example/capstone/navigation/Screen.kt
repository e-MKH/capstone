package com.example.capstone.navigation

sealed class Screen(val route: String) {
    object Article : Screen("article")
    object WordBook : Screen("wordbook")
    object Settings : Screen("settings")
    object Info : Screen("info")
    object EnglishNews : Screen("eng")
    object ArticleDetail : Screen("detail")
}