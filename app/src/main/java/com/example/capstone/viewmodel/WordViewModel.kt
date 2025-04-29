package com.example.capstone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.local.WordDatabase
import com.example.capstone.data.local.WordEntity
import kotlinx.coroutines.launch


class WordViewModel(application: Application) : AndroidViewModel(application) {

    private val wordDao = WordDatabase.getDatabase(application).wordDao()


    fun saveWord(word: String, meaning: String) {
        viewModelScope.launch {
            wordDao.insertWord(WordEntity(word = word, meaning = meaning))
        }
    }

    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            wordDao.deleteWord(word)
        }
    }

    suspend fun getAllWords(): List<WordEntity> {
        return wordDao.getAllWords()
    }
}