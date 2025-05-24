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

    // =============================================================================================
    // 단어 READ

    // 단어장 불러오기 : 정렬은 Repository에서 작성
    @Query(
        """SELECT * FROM vocabulary"""
    )
    suspend fun readAllVocabulary() : List<Vocabulary>

    // 언어별 단어장 불러오기
    @Query(
        """SELECT * FROM vocabulary WHERE language_code = :langCode"""
    )
    suspend fun readLanguageVocabulary(
        langCode: String
    ) : List<Vocabulary>

    // =============================================================================================
    // 단어 UPDATE

    // 즐겨찾기 등록
    // 변경된 column 개수 반환 (1 or 0)
    @Query(
        """UPDATE vocabulary SET is_favorite = :isFavorite WHERE vocabulary_id = :vocaId"""
    )
    suspend fun updateFavoriteStatus(
        isFavorite: Boolean,
        vocaId: Long
    ) : Long

    // =============================================================================================
    //단어 DELETE

    // 단일 삭제
    @Query(
        """DELETE FROM vocabulary WHERE vocabulary_id = :vocaId"""
    )
    suspend fun deleteVocabulary(
        vocaId:Long
    )
    
    // 복수 삭제
    @Query(
        """DELETE FROM vocabulary WHERE vocabulary_id IN (:vocaIdList)"""
    )
    suspend fun deleteVocabulary(
        vocaIdList: List<Long>
    )
}