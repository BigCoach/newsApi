package com.example.newsapitest.presentation.di

import com.example.newsapitest.data.repository.NewsRepoImpl
import com.example.newsapitest.domain.repository.NewsApiRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun bindsNewsRepo(newsRepoImpl: NewsRepoImpl): NewsApiRepo
}