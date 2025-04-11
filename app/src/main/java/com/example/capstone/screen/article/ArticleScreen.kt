package com.example.capstone.screen.article

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/**
 * ✅ [ArticleScreen]
 * 앱 메인 화면: 사용자에게 뉴스 언어를 선택하게 하는 버튼 UI입니다.
 * 각 버튼을 누르면 해당 언어에 맞는 뉴스 화면으로 이동합니다.
 *
 * @param navController Navigation을 위한 컨트롤러
 */
@Composable
fun ArticleScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttonModifier = Modifier
            .fillMaxWidth()
            .height(120.dp)

        // ✅ 영어 뉴스 버튼
        Button(
            onClick = { navController.navigate("eng") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCE5FF))
        ) {
            Text("영어 기사", fontSize = 20.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 일본어 뉴스 버튼
        Button(
            onClick = { navController.navigate("jap") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCE5FF))
        ) {
            Text("일본어 기사", fontSize = 20.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 중국어 뉴스 버튼
        Button(
            onClick = { navController.navigate("ch") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCE5FF))
        ) {
            Text("중국어 기사", fontSize = 20.sp, color = Color.White)
        }
    }
}

