package com.example.capstone.data.api.service

import com.example.capstone.data.model.EasyNewsArticle
import retrofit2.http.GET

interface EasyNewsService {
    @GET("/easy-news")
    suspend fun getEasyNews(): List<EasyNewsArticle>
}