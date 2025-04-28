package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedUrlViewModel : ViewModel() {
    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url

    fun setUrl(newUrl: String) {
        _url.value = newUrl
    }
}