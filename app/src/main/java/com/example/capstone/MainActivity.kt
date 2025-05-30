package com.example.capstone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.capstone.navigation.NavGraph
import com.example.capstone.navigation.Screen
import com.example.capstone.theme.SampleTheme
import com.example.capstone.viewmodel.*
import kotlinx.coroutines.launch
import com.example.capstone.data.local.AppDatabase
import com.example.capstone.data.local.entity.Code
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 언어 코드 선삽입 (DB용)
        val db = AppDatabase.getDatabase(applicationContext)
        val codeDao = db.codeDao()
        val defaultCodes = listOf(
            Code(code = "en", info = "영어"),
            Code(code = "ja", info = "일본어"),
            Code(code = "es", info = "스페인어")
        )
        CoroutineScope(Dispatchers.IO).launch {
            codeDao.insertAll(defaultCodes)
        }

        enableEdgeToEdge()
        setContent {
            SampleTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = when {
        currentRoute?.contains("ja") == true ->
            listOf(Screen.PrimaryNewsJa, Screen.SecondaryNewsJa)
        currentRoute?.contains("es") == true ->
            listOf(Screen.PrimaryNewsEs, Screen.SecondaryNewsEs)
        else ->
            listOf(Screen.PrimaryNews, Screen.SecondaryNews)
    }

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = {
                    Text(
                        when (screen) {
                            is Screen.PrimaryNews, Screen.PrimaryNewsJa, Screen.PrimaryNewsEs -> "My level"
                            else -> "Other Level"
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val sharedUrlViewModel: SharedUrlViewModel = viewModel()
    val sharedTextViewModel: SharedTextViewModel = viewModel()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.PrimaryNews.route, Screen.SecondaryNews.route,
        Screen.PrimaryNewsJa.route, Screen.SecondaryNewsJa.route,
        Screen.PrimaryNewsEs.route, Screen.SecondaryNewsEs.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("메뉴", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(label = { Text("Home") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Article.route)
                })
                NavigationDrawerItem(label = { Text("WordBook") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.WordBook.route)
                })
                NavigationDrawerItem(label = { Text("Settings") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Settings.route)
                })
                NavigationDrawerItem(label = { Text("Info") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Info.route)
                })
            }
        }
    ) {
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
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                sharedUrlViewModel = sharedUrlViewModel,
                sharedTextViewModel = sharedTextViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
