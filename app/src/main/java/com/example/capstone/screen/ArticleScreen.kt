package com.example.capstone.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

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

        Button(
            onClick = { navController.navigate("eng") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(32.dp)
        ) {
            Text("영어 기사", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp)) // 버튼 간 간격

        Button(
            onClick = { navController.navigate("jap") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(32.dp)
        ) {
            Text("일본어 기사", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("ch") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(32.dp)
        ) {
            Text("중국어 기사", fontSize = 20.sp)
        }
    }
}

