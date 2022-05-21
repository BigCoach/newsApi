package com.example.newsapitest.data.network

import com.example.newsapitest.data.model.Article
import com.example.newsapitest.data.model.BaseNewsApi
import com.example.newsapitest.data.model.BaseSourcesApi
import com.example.newsapitest.data.model.Source
import retrofit2.Response
import retrofit2.http.*

interface NewsApiService {

    @GET("/v2/everything")
    suspend fun getNews(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 30,
        @Query("sources") sources: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("sortBy") sortBy: String? = "popularity",
        @Query("q") q: String? = "bitcoin",
        @Header("X-Api-Key") apiKey: String,
    ) : Response<BaseNewsApi<List<Article>>>?


    @GET("/v2/top-headlines/sources")
    suspend fun getSources(
        @Header("X-Api-Key") apiKey: String,
    ) : Response<BaseSourcesApi<List<Source>>>?
}