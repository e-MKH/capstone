package com.example.capstone.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "languages",
    primaryKeys = [""]
)
data class Language(
    @PrimaryKey
    val code : String,  // 국가 코드(kr, jp, en 등)
    val name : String   // 표시명(한국어, 영어, 일본어 등)
)
