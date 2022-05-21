package com.example.newsapitest.domain.usecases

import com.example.newsapitest.domain.repository.NewsApiRepo
import javax.inject.Inject

class ArticleLikeClickedUseCase @Inject constructor(
    private val newsApiRepo: NewsApiRepo
) {
    suspend fun onArticleLikeClick(localId: Long){
        newsApiRepo.onArticleLikeClicked(localId)
    }
}