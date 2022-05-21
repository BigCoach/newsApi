package com.example.newsapitest.domain.usecases

import androidx.lifecycle.LiveData
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.domain.repository.NewsApiRepo
import javax.inject.Inject

class GetFavouritesUseCase @Inject constructor(
    private val newsApiRepo: NewsApiRepo
) {
    suspend fun getFavourites() : LiveData<List<Article>> = newsApiRepo.getFavouritesLiveData()
}