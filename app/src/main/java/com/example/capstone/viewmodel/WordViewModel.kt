package com.example.capstone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.local.WordDatabase
import com.example.capstone.data.local.entity.WordEntity
import kotlinx.coroutines.launch

/**
 * [WordViewModel]
 * 단어장 기능을 담당하는 ViewModel
 * - Room DB를 통해 단어 데이터를 저장/조회/삭제
 * - Application context가 필요한 경우를 대비해 AndroidViewModel 사용
 */
class WordViewModel(application: Application) : AndroidViewModel(application) {

    // DAO 객체 생성 (Room DB에 접근)
    private val wordDao = WordDatabase.getDatabase(application).wordDao()

    /**
     * 단어 저장 함수
     * @param word 저장할 단어 원문
     * @param meaning 번역된 단어 의미
     */
    fun saveWord(word: String, meaning: String) {
        viewModelScope.launch {
            wordDao.insertWord(WordEntity(word = word, meaning = meaning))
        }
    }

    /**
     * 단어 삭제 함수
     * @param word 삭제할 단어 객체 (WordEntity)
     */
    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            wordDao.deleteWord(word)
        }
    }

    /**
     * 모든 단어 리스트 조회 함수
     * @return 저장된 모든 단어 리스트
     */
    suspend fun getAllWords(): List<WordEntity> {
        return wordDao.getAllWords()
    }
}
