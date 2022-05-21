package com.example.newsapitest.data.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.data.model.ArticleRemoteKeys
import com.example.newsapitest.data.network.NewsApiService
import com.example.newsapitest.util.Constants
import okio.IOException
import timber.log.Timber


@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator constructor(
    private val sourceFilter: String? = null,
    private val dateFrom: String? = null,
    private val dateTo: String? = null,
    private val sortBy: String? = null,
    private val database: NewsDb,
    private val newsApiService: NewsApiService,
) : RemoteMediator<Int, Article>() {
    private val articlesDao = database.articlesDao()
    private val remoteKeysDao = database.articleRemoteKeysDao()

    companion object {
        const val INITIAL_PAGE = 1
        const val TAG_ARTICLES_MEDIATOR = "TAG_ARTICLES_MEDIATOR"
    }

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("load starts")
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("loadType: $loadType")
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("state anchorposition: ${state.anchorPosition}")
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("state first: ${state.firstItemOrNull()}")
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("state last: ${state.lastItemOrNull()}")
            val pageToLoad: Int = when (loadType) {
                LoadType.REFRESH -> {
                    getClosestRemoteKey(state)?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    if (state.anchorPosition == null && state.firstItemOrNull() == null && state.lastItemOrNull() == null) return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextPage ?: return MediatorResult.Success(true)
                }
            }

            val response = newsApiService.getNews(
                page = pageToLoad,
                sources = sourceFilter,
                from = dateFrom,
                to = dateTo,
                sortBy = sortBy,
                apiKey = Constants.API_KEY
            )
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("response: $response")
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("response?.body()?.articles?.size: ${response?.body()?.articles?.size}")
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("state.config.pageSize: ${state.config.pageSize}")

            val endOfPaginationReached =
                (response?.body()?.articles?.size ?: 0) < state.config.pageSize
            Timber.tag(TAG_ARTICLES_MEDIATOR).d("endOfPaginationReached: $endOfPaginationReached")
            val list = response?.body()?.articles ?: arrayListOf()

            database.withTransaction {
                // if refreshing, clear table and start over
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    articlesDao.clearTable()
                }
                articlesDao.insertAll(list)

                val keys = list.map {
                    ArticleRemoteKeys(
                        it.publishedAt ?: "",
                        pageToLoad + 1,
                        pageToLoad - 1
                    )
                }
                remoteKeysDao.insertAll(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            e.printStackTrace()
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Article>): ArticleRemoteKeys? {
        Timber.tag(TAG_ARTICLES_MEDIATOR).d("getRemoteKeyForFirstItem")
        val firstItem = state.firstItemOrNull()?.let { news ->
            database.withTransaction { remoteKeysDao.remoteKeysByArticleDate(news.publishedAt ?: "") }
        }
        Timber.tag(TAG_ARTICLES_MEDIATOR).d("getRemoteKeyForFirstItem firstItem: $firstItem")
        return firstItem
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Article>): ArticleRemoteKeys? {
        Timber.tag(TAG_ARTICLES_MEDIATOR).d("getRemoteKeyForLastItem")
        val lastItem = state.lastItemOrNull()?.let { news ->
            database.withTransaction { remoteKeysDao.remoteKeysByArticleDate(news.publishedAt ?: "") }
        }
        Timber.tag(TAG_ARTICLES_MEDIATOR).d("getRemoteKeyForLastItem lastItem: $lastItem")
        return lastItem
    }

    /*
    Get closest ArticleRemoteKeys for the first visible position in displayed list
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, Article>): ArticleRemoteKeys? {
        Timber.tag(TAG_ARTICLES_MEDIATOR).d("getClosestRemoteKey state")
        val closestItem = state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.publishedAt?.let { date ->
                database.withTransaction { remoteKeysDao.remoteKeysByArticleDate(date) }
            }
        }
        Timber.tag(TAG_ARTICLES_MEDIATOR).d("closestItem: $closestItem")
        return closestItem
    }


}