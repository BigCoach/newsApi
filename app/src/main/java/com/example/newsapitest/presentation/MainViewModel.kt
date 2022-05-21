package com.example.newsapitest.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapitest.MainApplication
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.data.model.Source
import com.example.newsapitest.domain.usecases.ArticleLikeClickedUseCase
import com.example.newsapitest.domain.usecases.GetArticlesPaginatedUseCase
import com.example.newsapitest.domain.usecases.GetFavouritesUseCase
import com.example.newsapitest.domain.usecases.GetSourcesUseCase
import com.example.newsapitest.presentation.ui.state.ArticlesScreenState
import com.example.newsapitest.util.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getArticlesPaginatedUseCase: GetArticlesPaginatedUseCase,
    private val articleLikeClickedUseCase: ArticleLikeClickedUseCase,
    private val getSourcesUseCase: GetSourcesUseCase,
    private val getFavouritesUseCase: GetFavouritesUseCase,
    ) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    init {
        getArticlesPaginatedFlow()
        getFavouritesLiveData()
        getSources()
        getSelectedSources()
        getAllSources()
    }

    private val _articlesScreenState = mutableStateOf<ArticlesScreenState>(ArticlesScreenState.Content)
    val articlesScreenState: MutableState<ArticlesScreenState>
        get() = _articlesScreenState

    var articlesPagingFlow: Flow<PagingData<Article>>? = null

    private var _favoritesLiveData: MutableLiveData<List<Article>> = MutableLiveData(null)
    var favoritesLiveData: LiveData<List<Article>> = _favoritesLiveData

    private var _sourcesLiveData: MutableLiveData<List<Source>> = MutableLiveData(null)
    var sourcesLiveData: LiveData<List<Source>> = _sourcesLiveData

    private var _selectedSources: MutableLiveData<List<Source>> = MutableLiveData(null)
    var selectedSourcesLiveData: LiveData<List<Source>> = _selectedSources

    private fun getSources(){
        launch {
            getSourcesUseCase.loadSourcesFromServer()
        }
    }

    fun updateSource(id: String, isSelected: Boolean){
        launch {
            getSourcesUseCase.updateSource(id, isSelected)
        }
    }

    private fun getSelectedSources(){
        launch {
            selectedSourcesLiveData = getSourcesUseCase.getSelectedSources()
        }
    }

    private fun getAllSources(){
        launch {
            sourcesLiveData = getSourcesUseCase.getAllSources()
        }
    }

    private fun getFavouritesLiveData(){
        launch(Dispatchers.Main) {
            favoritesLiveData = getFavouritesUseCase.getFavourites()
        }
    }

    private fun getArticlesPaginatedFlow(sourceFilter: String? = null,
                                         dateFrom: String? = null,
                                         dateTo: String? = null,
                                         sortBy: String? = null
    ) {
        if (articlesPagingFlow == null) articlesPagingFlow =
            getArticlesPaginatedUseCase.getArticlesPaginated(sourceFilter, dateFrom, dateTo, sortBy).flow.cachedIn(viewModelScope)
    }

    fun onLikeClicked(articleLocalId: Long){
        launch(Dispatchers.IO) {
            articleLikeClickedUseCase.onArticleLikeClick(articleLocalId)
        }
    }

    fun saveFileToPictures(url: String) {
        launch(Dispatchers.IO) {
            FileUtils.saveImageToDownloadsFromUrl(
                MainApplication.instance,
                url
            )
        }
    }

}