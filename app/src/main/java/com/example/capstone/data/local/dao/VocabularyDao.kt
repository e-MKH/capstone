package com.example.capstone.data.local.dao

import androidx.room.*
import com.example.capstone.data.local.entity.Vocabulary

@Dao
interface VocabularyDao {

    // 단어 INSERT
    // 성공 반환값 = PK / 실패 반환값 = 0
    @Query(
        """INSERT OR IGNORE INTO vocabulary (word, language_code, meaning, pronunciation, is_favorite) VALUES (:word, :langCode, :meaning, :pronunciation, :isFavorite)"""
    )
    suspend fun insertVocabulary(
        word: String,
        langCode: String,
        meaning:String,
        pronunciation: String,
        isFavorite:Boolean
    ) : Long


    // 단어 DELETE(단일)
    @Query(
        """DELETE FROM vocabulary WHERE vocabulary_id = :vocaId"""
    )
    suspend fun deleteVocabulary(
        vocaId:Long
    )
    
    // 단어 DELETE(복수)
    @Query(
        """DELETE FROM vocabulary WHERE vocabulary_id IN (:vocaIdList)"""
    )
    suspend fun deleteVocabulary(
        vocaIdList: List<Long>
    )
}