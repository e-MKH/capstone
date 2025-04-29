package com.example.capstone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "word_table")
data class WordEntity(
    @PrimaryKey val word: String,
    val meaning: String? = null
)