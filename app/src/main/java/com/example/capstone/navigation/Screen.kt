package com.example.capstone.navigation

/**
 * [Screen]
 * 앱 내에서 사용하는 각 화면의 route를 정의한 sealed class
 * - 화면 이동 시 문자열을 직접 입력하지 않고, 이 객체를 참조함으로써 안전성 향상
 * - Jetpack Navigation에서 경로(route)로 사용됨
 */
sealed class Screen(val route: String) {

    /** 언어 선택 메인 화면 (뉴스 언어 선택 버튼) */
    object Article : Screen("article")

    /** 단어장 화면 */
    object WordBook : Screen("wordbook")

    /** 설정 화면 */
    object Settings : Screen("settings")

    /** ℹ앱 정보 화면 */
    object Info : Screen("info")

    /** 영어 뉴스 리스트 화면 */
    object EnglishNews : Screen("eng")

    /** 기사 본문 */
    object ArticleDetail : Screen("detail")

    /** 일본어 뉴스 리스트 화면 */
    object JapaneseNews : Screen("japanNews")

}
