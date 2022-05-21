package com.example.newsapitest.domain.usecases

import androidx.paging.ExperimentalPagingApi
import com.example.newsapitest.domain.repository.NewsApiRepo
import javax.inject.Inject

class GetArticlesPaginatedUseCase @Inject constructor(
    private val newsApiRepo: NewsApiRepo
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getArticlesPaginated(sourceFilter: String?,
                             dateFrom: String?,
                             dateTo: String?,
                             sortBy: String?,
    ) = newsApiRepo.observeArticlesPaginated(sourceFilter, dateFrom, dateTo, sortBy)

}