package com.example.capstone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ✅ [WordEntity]
 * Room DB에 저장되는 단어 데이터 모델입니다.
 *
 * @property word 저장할 단어 (기본 키 역할)
 */
@Entity(tableName = "word_table")
data class WordEntity(
    @PrimaryKey val word: String // ✅ 단어 자체가 기본 키이므로 중복 저장 방지됨
)

