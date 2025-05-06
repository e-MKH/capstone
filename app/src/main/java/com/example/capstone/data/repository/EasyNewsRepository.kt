package com.example.capstone.data.repository

import com.example.capstone.data.api.EasyNewsService
import com.example.capstone.data.model.EasyNewsArticle
class EasyNewsRepository(private val api: EasyNewsService) {
    suspend fun fetchEasyNews(): List<EasyNewsArticle> {
        return api.getEasyNews()
    }
}

