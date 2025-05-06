package com.example.capstone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [WordEntity]
 * Room 데이터베이스에 저장될 단어 데이터를 나타내는 엔터티 클래스
 * - 단어 자체가 기본 키로 사용되어 중복 저장을 방지
 * - 의미 필드는 선택 사항이며, 번역된 단어를 저장할 수 있음
 * @property word    단어 원문 (Primary Key)
 * @property meaning 번역된 의미 (nullable)
 */
@Entity(tableName = "word_table")
data class WordEntity(
    @PrimaryKey val word: String,
    val meaning: String? = null
)
