package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedTextViewModel : ViewModel() {

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    fun setText(newText: String, sourceUrl: String? = null) {
        val isEasyNews = sourceUrl?.contains("newsinlevels", ignoreCase = true) == true

        val cleanedText = if (isEasyNews) {
            val cutKeywords = listOf(
                "3000 WORDS WITH NEWS IN LEVEL",
                "words:",
                "can watch the original video",
                "Go to Level"
            )

            // 줄별 정제: 출처, 날짜, 시간 제거
            val lines = newText.lines().filterNot {
                it.contains("level", ignoreCase = true) ||
                        it.contains("published", ignoreCase = true) ||
                        it.contains("news in levels", ignoreCase = true) ||
                        it.matches(Regex(""".*\d{1,2}:\d{2}.*""")) ||  // 시간 제거
                        it.matches(Regex(""".*\d{4}-\d{2}-\d{2}.*"""))  // 날짜 제거
            }

            // 꼬리말 제거
            val joined = lines.joinToString("\n")
            val cutoffText = cutKeywords.fold(joined) { acc, keyword ->
                if (acc.contains(keyword)) acc.substringBefore(keyword).trim() else acc
            }

            // 불필요한 공백 정리
            cutoffText
                .replace(Regex("""\s{2,}"""), " ")
                .replace(Regex("""\n{2,}"""), "\n")
                .trim()
        } else {
            newText.trim()
        }

        _text.value = cleanedText
    }

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }
}


