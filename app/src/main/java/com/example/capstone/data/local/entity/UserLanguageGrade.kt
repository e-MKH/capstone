package com.example.capstone.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDate

@Entity(
    tableName = "user_language_grade",
    primaryKeys = ["id"],
    foreignKeys = []
)
data class UserLanguageGrade(
    @ColumnInfo(name = "id")
    val id : String,
    @ColumnInfo(name = "lang_code")
    val langCode : String,
    @ColumnInfo(name = "grade")
    val grade : String,
    @ColumnInfo(name = "is_updated")
    val isUpdated : LocalDate
)
