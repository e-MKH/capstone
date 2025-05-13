package com.example.capstone.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDate

@Entity(
    tableName = "user",
    primaryKeys = ["id"]
)
data class User (
    @ColumnInfo(name = "id")
    val id : String,
    @ColumnInfo(name = "name")
    val name : String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "birth_date")
    val birthDate : LocalDate,
    @ColumnInfo(name = "join_date")
    val joinDate : LocalDate
)