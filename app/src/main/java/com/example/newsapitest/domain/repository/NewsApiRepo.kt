package com.example.newsapitest.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.data.model.Source

interface NewsApiRepo {

    suspend fun onArticleLikeClicked(localId: Long)
    suspend fun getFavouritesLiveData() : LiveData<List<Article>>

    suspend fun getNews(
        sources: String? = null,
        from: String? = null,
        to: String? = null,
        sortBy: String? = null,
        page: Int = 1,
        pageSize: Int = 25,
    ) : Result<List<Article>>

    @ExperimentalPagingApi
    fun observeArticlesPaginated(
        sourceFilter: String?,
        dateFrom: String?,
        dateTo: String?,
        sortBy: String?,
    ): Pager<Int, Article>

    suspend fun getSources()
    suspend fun updateSource(sourceId: String, isSelected: Boolean)
    suspend fun getSelectedSourcesLiveData() : LiveData<List<Source>>
    suspend fun getAllSourcesLiveData() : LiveData<List<Source>>

}