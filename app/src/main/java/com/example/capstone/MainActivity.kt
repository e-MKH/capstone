package com.example.capstone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.capstone.navigation.NavGraph
import com.example.capstone.ui.theme.SampleTheme
import com.example.capstone.viewmodel.SharedUrlViewModel
import com.example.capstone.viewmodel.SharedTextViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.unit.dp

/**
 * [MainActivity]
 * 앱의 진입점이자 전체 UI를 Compose로 구성하는 액티비티
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme {
                MyApp() // 앱 메인 UI 시작
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()

    // 공유 ViewModel 초기화
    val sharedUrlViewModel: SharedUrlViewModel = viewModel()
    val sharedTextViewModel: SharedTextViewModel = viewModel()

    // Drawer 상태 및 코루틴 스코프 설정
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 사이드 메뉴 Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "메뉴",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                // 메뉴 항목: 홈
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("article")
                    }
                )
                // 메뉴 항목: 단어장
                NavigationDrawerItem(
                    label = { Text("WordBook") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("wordbook")
                    }
                )
                // 메뉴 항목: 설정
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("settings")
                    }
                )
                // 메뉴 항목: 앱 정보
                NavigationDrawerItem(
                    label = { Text("Info") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("info")
                    }
                )
            }
        }
    ) {
        // 상단 앱바 + 본문 영역을 포함하는 Scaffold
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("NewsPeed") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "메뉴")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // 실제 화면 라우팅은 NavGraph에서 처리
            NavGraph(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel,
                sharedTextViewModel = sharedTextViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
