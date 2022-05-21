package com.example.newsapitest.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapitest.data.model.ArticleRemoteKeys

@Dao
abstract class ArticleRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(remoteKey: List<ArticleRemoteKeys>)

    @Query("SELECT * FROM ArticleRemoteKeys WHERE publishedAt = :publishedAt")
    abstract fun remoteKeysByArticleDate(publishedAt: String): ArticleRemoteKeys?

    @Query("DELETE FROM ArticleRemoteKeys WHERE publishedAt = :publishedAt")
    abstract fun clearRemoteKeysForArticle(publishedAt: Long)

    @Query("DELETE FROM ArticleRemoteKeys")
    abstract fun clearRemoteKeys()
}