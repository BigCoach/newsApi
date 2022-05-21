package com.example.newsapitest.presentation.ui.state

sealed class ArticlesScreenState {
    object Loading : ArticlesScreenState()
    object Error : ArticlesScreenState()
    object Content : ArticlesScreenState()
}