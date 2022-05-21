package com.example.newsapitest.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.newsapitest.data.db.ArticleDao
import com.example.newsapitest.data.db.ArticleRemoteMediator
import com.example.newsapitest.data.db.NewsDb
import com.example.newsapitest.data.db.SourceDao
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.data.model.Source
import com.example.newsapitest.data.network.NewsApiService
import com.example.newsapitest.domain.repository.NewsApiRepo
import com.example.newsapitest.util.Constants
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class NewsRepoImpl @Inject constructor(
    private val articleDao: ArticleDao,
    private val sourceDao: SourceDao,
    private val db: NewsDb,
    private val newsApiService: NewsApiService
): NewsApiRepo {

    override suspend fun getNews(
        sources: String?,
        from: String?,
        to: String?,
        sortBy: String?,
        page: Int,
        pageSize: Int
    ): Result<List<Article>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavouritesLiveData(): LiveData<List<Article>> = articleDao.getFavouritesLivedata()

    override suspend fun getSources() {
        Timber.d("getSources repo")
      try {
          val sourcesResponse = newsApiService.getSources(Constants.API_KEY)
          Timber.d("sourcesResponse: $sourcesResponse")
          Timber.d("sourcesResponse: ${sourcesResponse?.body()?.sources}")
          val sources = sourcesResponse?.body()?.sources ?: return
          sourceDao.insertAll(sources)
      } catch (e: Exception){
          e.printStackTrace()
      }
    }

    override suspend fun updateSource(sourceId: String, isSelected: Boolean) {
        sourceDao.updateSource(sourceId, isSelected)
    }

    override suspend fun getSelectedSourcesLiveData(): LiveData<List<Source>> {
        return sourceDao.getSelectedSources()
    }

    override suspend fun getAllSourcesLiveData(): LiveData<List<Source>> {
        return sourceDao.getAllSources()
    }

    override suspend fun onArticleLikeClicked(localId: Long) {
        val article = articleDao.getArticleByLocalId(localId) ?: return
        articleDao.updateArticle(localId, !article.inFavorites)
    }

    /**
     * TracksRemoteMediator should observe the data from database and load the appropriate page of data
     * for remote source.
     */
    @ExperimentalPagingApi
    override fun observeArticlesPaginated(
        sourceFilter: String?,
        dateFrom: String?,
        dateTo: String?,
        sortBy: String?,
    ): Pager<Int, Article> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 30
            ),
            remoteMediator = ArticleRemoteMediator(
                sourceFilter = sourceFilter,
                dateFrom = dateFrom,
                dateTo = dateTo,
                sortBy = sortBy,
                database = db,
                newsApiService = newsApiService
            )
        ) {
            articleDao.getArticlesPagingSource()
        }
    }

}