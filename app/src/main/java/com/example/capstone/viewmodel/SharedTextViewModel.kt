package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedTextViewModel : ViewModel() {
    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    fun setText(newText: String) {
        _text.value = newText
    }

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }
}
