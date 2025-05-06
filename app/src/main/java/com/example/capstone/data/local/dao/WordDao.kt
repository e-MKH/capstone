package com.example.capstone.data.local.dao

import androidx.room.*
import com.example.capstone.data.local.entity.WordEntity

/**
 * ✅ [WordDao]
 * Room 데이터베이스에서 단어 테이블(word_table)에 접근하기 위한 DAO입니다.
 * - insert / select / delete 기본 기능을 제공합니다.
 */
@Dao
interface WordDao {

    /**
     * ✅ 단어 저장
     *
     * @param word 저장할 단어 객체
     * onConflict = IGNORE → 이미 존재하는 단어는 무시하고 삽입하지 않음
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: WordEntity)

    /**
     * ✅ 전체 단어 조회
     *
     * @return 저장된 모든 단어 리스트
     */
    @Query("SELECT * FROM word_table")
    suspend fun getAllWords(): List<WordEntity>

    /**
     * ✅ 단어 삭제
     *
     * @param word 삭제할 단어 객체
     */
    @Delete
    suspend fun deleteWord(word: WordEntity)
}

