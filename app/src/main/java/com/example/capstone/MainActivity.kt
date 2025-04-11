package com.example.capstone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.capstone.screen.*
import com.example.capstone.screen.article.ArticleScreen
import com.example.capstone.screen.article.engPart.LanguageArticleScreenEng
// import com.example.capstone.screen.article.japPart.LanguageArticleScreenJap
// import com.example.capstone.screen.article.chPart.LanguageArticleScreenCh
import com.example.capstone.ui.theme.SampleTheme
import kotlinx.coroutines.launch

/**
 * ✅ 앱의 메인 액티비티
 * - Drawer 메뉴를 사용하여 화면 전환 제공
 * - 각 컴포저블 화면을 NavHost로 연결
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme {
                MyDrawerApp()
            }
        }
    }
}

/**
 * ✅ [Screen]
 * Drawer 메뉴에 표시될 각 화면 정보 (아이콘 + 라우트)
 */
sealed class Screen(val name: String, val icon: ImageVector, val route: String) {
    object Article : Screen("Article", Icons.Filled.Home, "article")
    object WordBook : Screen("WordBook", Icons.Filled.List, "wordbook")
    object Settings : Screen("Settings", Icons.Filled.Settings, "settings")
    object Info : Screen("Info", Icons.Filled.Info, "info")
}

/**
 * ✅ [MyDrawerApp]
 * 전체 앱의 화면 구조를 구성하는 컴포저블 함수
 * - Drawer 메뉴 구현
 * - NavHost로 각 화면 라우팅 연결
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDrawerApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    // ✅ Drawer 메뉴에 표시할 화면 목록
    val screens = listOf(Screen.Article, Screen.WordBook, Screen.Settings, Screen.Info)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.name) },
                        selected = false, // 선택 상태는 현재 구현 없음
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(screen.route)
                        }
                    )
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Article.route,
                    modifier = Modifier.padding(padding)
                ) {
                    // ✅ 각 라우트별 화면 연결
                    composable(Screen.Article.route) { ArticleScreen(navController) } // 언어 선택 화면
                    composable(Screen.WordBook.route) { WordBookScreen() }           // 단어장 화면
                    composable(Screen.Settings.route) { Settings() }
                    composable(Screen.Info.route) { Info() }

                    // ✅ 언어별 뉴스 화면
                    composable("eng") { LanguageArticleScreenEng(navController) }
                    // composable("jap") { LanguageArticleScreenJap("일본어", navController) }
                    // composable("ch") { LanguageArticleScreenCh("중국어", navController) }
                }
            }
        }
    )
}

