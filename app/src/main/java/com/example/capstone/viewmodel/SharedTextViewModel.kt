package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedTextViewModel : ViewModel() {

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language

    /**
     * ✅ 기사 본문과 언어, 제목을 함께 설정
     */
    fun setText(newText: String, lang: String, newTitle: String) {
        _text.value = newText.trim()
        _language.value = lang
        _title.value = newTitle
    }
}
