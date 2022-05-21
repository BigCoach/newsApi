package com.example.newsapitest.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.newsapitest.data.model.Article

@Dao
abstract class ArticleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertArticle(Article: Article) : Long

    @Query("DELETE FROM Article WHERE inFavorites = :inFavorites")
    abstract fun clearTable(inFavorites: Boolean = false)

    @Delete
    abstract fun deleteArticle(article: Article)

    @Query("SELECT * FROM Article WHERE localId = :id")
    abstract fun getArticleById(id: Int): Article?

    @Query("SELECT * FROM Article WHERE localId = :id")
    abstract fun getArticleByLocalId(id: Long): Article?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(users: List<Article>)

    @Query("SELECT * FROM Article")
    abstract fun getArticlesPagingSource(): PagingSource<Int, Article>

    @Query("SELECT * FROM Article WHERE inFavorites = 1")
    abstract fun getFavouritesLivedata() : LiveData<List<Article>>

    @Query("UPDATE Article SET inFavorites = :inFavorites WHERE localId = :articleId")
    abstract fun updateArticle(
        articleId: Long,
        inFavorites: Boolean
    )

}