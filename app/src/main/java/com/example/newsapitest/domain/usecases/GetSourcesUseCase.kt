package com.example.newsapitest.domain.usecases

import androidx.lifecycle.LiveData
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.data.model.Source
import com.example.newsapitest.domain.repository.NewsApiRepo
import javax.inject.Inject

class GetSourcesUseCase @Inject constructor(
    private val newsApiRepo: NewsApiRepo
) {

    suspend fun loadSourcesFromServer(){
        return newsApiRepo.getSources()
    }
    suspend fun updateSource(sourceId: String, isSelected: Boolean){
        newsApiRepo.updateSource(sourceId, isSelected)
    }
    suspend fun getSelectedSources() : LiveData<List<Source>> = newsApiRepo.getSelectedSourcesLiveData()
    suspend fun getAllSources() : LiveData<List<Source>> = newsApiRepo.getAllSourcesLiveData()

}