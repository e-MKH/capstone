package com.example.capstone.data.local.dao

import androidx.room.*

@Dao
interface UserDao {
    // User INSERT
    @Query(
        """INSERT INTO user ()"""
    )
    suspend fun registUser()
}