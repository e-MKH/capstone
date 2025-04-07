package com.example.capstone.screen
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.capstone.ui.theme.SampleTheme

@Composable
fun jap() {

    Text(
        text = "jap",
        fontSize =30.sp
    )
}

@Preview(showBackground = true)
@Composable
fun japPreview() {
    SampleTheme {
        jap()
    }
}