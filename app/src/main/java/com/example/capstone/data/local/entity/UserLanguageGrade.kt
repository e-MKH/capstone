package com.example.capstone.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate

@Entity(
    tableName = "user_language_grade",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["name"],
            childColumns = ["name"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Code::class,
            parentColumns = ["code"],
            childColumns = ["language_code"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class UserLanguageGrade(
    @ColumnInfo(name = "name")
    val name : String,
    @ColumnInfo(name = "language_code")
    val langCode : String,
    @ColumnInfo(name = "grade")
    val grade : String,
    @ColumnInfo(name = "is_updated")
    val isUpdated : LocalDate
)
