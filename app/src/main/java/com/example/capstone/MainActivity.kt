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
import com.example.capstone.viewmodel.SharedTextViewModel
import com.example.capstone.viewmodel.SharedUrlViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.PrimaryNews,
        Screen.SecondaryNews
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
                icon = {
                    Icon(Icons.Default.List, contentDescription = null)
                },
                label = {
                    Text(screen.route.replace("_", " ").replaceFirstChar { it.uppercase() })
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
        Screen.PrimaryNews.route,
        Screen.SecondaryNews.route
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
