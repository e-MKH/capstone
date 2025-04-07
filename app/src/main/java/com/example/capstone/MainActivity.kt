package com.example.capstone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.capstone.screen.ArticleScreen
import com.example.capstone.screen.Info
import com.example.capstone.screen.LanguageArticleScreen
import com.example.capstone.screen.SaveList
import com.example.capstone.screen.Settings
import com.example.capstone.ui.theme.SampleTheme
import kotlinx.coroutines.launch

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

sealed class Screen(val name: String, val icon: ImageVector, val route: String) {
    object Article : Screen("Article", Icons.Filled.Home, "article")
    object SaveList : Screen("SaveList", Icons.Filled.List, "saveList")
    object Settings : Screen("Settings", Icons.Filled.Settings, "settings")
    object Info : Screen("Info", Icons.Filled.Info, "info")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDrawerApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val screens = listOf(Screen.Article, Screen.SaveList, Screen.Settings, Screen.Info)

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
                        selected = false,
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
                    composable(Screen.Article.route) { ArticleScreen(navController) }
                    composable(Screen.SaveList.route) { SaveList() }
                    composable(Screen.Settings.route) { Settings() }
                    composable(Screen.Info.route) { Info() }
                    composable("eng") { LanguageArticleScreen("영어", navController) }
                    composable("jap") { LanguageArticleScreen("일본어", navController) }
                    composable("ch") { LanguageArticleScreen("중국어", navController) }
                }
            }
        }
    )
}


