package com.example.capstone.data.local

import androidx.room.*


@Dao
interface WordDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: WordEntity)


    @Query("SELECT * FROM word_table")
    suspend fun getAllWords(): List<WordEntity>


    @Delete
    suspend fun deleteWord(word: WordEntity)
}
