package com.example.capstone.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_table",
    primaryKeys = ["word"]
)
data class Vocabulary(
    val word: String,   // 단어
    val languageCode : String,  // 언어
    val meaning : String,    // 의미
    val isFavorite : Boolean = false    // 즐겨찾기
)
