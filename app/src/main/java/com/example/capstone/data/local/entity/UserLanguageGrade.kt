package com.example.capstone.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "user_language_grade",
    primaryKeys = ["id"],
    foreignKeys = []
)
data class UserLanguageGrade(
    @ColumnInfo(name = "id")
    val id : String
)
