package com.example.capstone.screen.article

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capstone.navigation.Screen

@Composable
fun ArticleScreen(
    navController: NavController
) {
    val cardShape = RoundedCornerShape(32.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArticleButton(
            text = "영어 기사",
            onClick = { navController.navigate(Screen.EnglishNews.route) },
            shape = cardShape
        )
        Spacer(modifier = Modifier.height(16.dp))
        ArticleButton(
            text = "일본어 기사",
            onClick = { /* 일본어 이동 예정 */ },
            shape = cardShape
        )
        Spacer(modifier = Modifier.height(16.dp))
        ArticleButton(
            text = "중국어 기사",
            onClick = { /* 중국어 이동 예정 */ },
            shape = cardShape
        )
    }
}

@Composable
fun ArticleButton(
    text: String,
    onClick: () -> Unit,
    shape: RoundedCornerShape
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFAEDFF7),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

