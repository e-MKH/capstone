package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * [SharedTextViewModel]
 * 기사 본문 텍스트와 제목을 공유하기 위한 ViewModel
 * - 뉴스 상세 화면(ArticleDetailScreen)에서 사용됨
 * - NavGraph를 통해 Navigation 간 안전하게 데이터 전달
 */
class SharedTextViewModel : ViewModel() {

    // 기사 본문 텍스트 상태
    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    // 기사 제목 상태
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    /** 기사 본문 텍스트 설정 함수 */
    fun setText(newText: String) {
        _text.value = newText
    }

    /** 기사 제목 설정 함수 */
    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }
}

