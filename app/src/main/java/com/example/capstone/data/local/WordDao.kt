package com.example.capstone.data.local

import androidx.room.*

/**
 * ✅ [WordDao]
 * 단어장을 위한 데이터 접근 객체(DAO)입니다.
 * Room DB를 통해 단어를 삽입, 조회, 삭제하는 쿼리를 정의합니다.
 */
@Dao
interface WordDao {

    /**
     * ✅ 단어 저장
     * 중복 단어는 무시하고 새 단어만 저장합니다.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: WordEntity)

    /**
     * ✅ 단어 전체 조회
     * 저장된 모든 단어를 리스트 형태로 반환합니다.
     */
    @Query("SELECT * FROM word_table")
    suspend fun getAllWords(): List<WordEntity>

    /**
     * ✅ 단어 삭제
     * 특정 단어(WordEntity 객체)를 DB에서 제거합니다.
     */
    @Delete
    suspend fun deleteWord(word: WordEntity)
}
