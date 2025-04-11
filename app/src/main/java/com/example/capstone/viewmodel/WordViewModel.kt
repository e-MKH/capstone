package com.example.capstone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.local.WordDatabase
import com.example.capstone.data.local.WordEntity
import kotlinx.coroutines.launch

/**
 * ✅ [WordViewModel]
 * 단어장을 관리하는 ViewModel입니다.
 * Room 데이터베이스에 접근해 단어 저장/삭제/조회 기능을 제공합니다.
 *
 * @param application Android Context를 사용하기 위해 AndroidViewModel 상속
 */
class WordViewModel(application: Application) : AndroidViewModel(application) {

    // ✅ DB 인스턴스에서 DAO 가져오기
    private val wordDao = WordDatabase.getDatabase(application).wordDao()

    /**
     * ✅ 단어 저장 함수
     * - 클릭한 단어를 Room DB에 저장
     * - 동일한 단어는 중복 저장되지 않음 (WordEntity에서 PrimaryKey 사용 중)
     */
    fun saveWord(word: String) {
        viewModelScope.launch {
            wordDao.insertWord(WordEntity(word))
        }
    }

    /**
     * ✅ 단어 삭제 함수
     * - 단어 객체를 전달받아 삭제
     */
    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            wordDao.deleteWord(word)
        }
    }

    /**
     * ✅ 저장된 단어 전체 불러오기
     * - 단어장 리스트 화면에서 사용
     */
    suspend fun getAllWords(): List<WordEntity> {
        return wordDao.getAllWords()
    }
}
