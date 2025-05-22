package com.example.capstone.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "Article",
    primaryKeys = [""]
)
data class Article(
    val title : String,
    val description : String,
    val content : String,
    val langCode : String,
    val translation : String,
    val isFavorite : Boolean = false,
    val difficulty : String
)