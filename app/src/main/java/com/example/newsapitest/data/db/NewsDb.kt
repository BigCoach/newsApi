package com.example.newsapitest.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.data.model.ArticleRemoteKeys
import com.example.newsapitest.data.model.Source

@Database(
    entities = [
        Article::class,
        ArticleRemoteKeys::class,
    Source::class
    ],
    version = 1,
)

abstract class NewsDb : RoomDatabase(){
    abstract fun articlesDao(): ArticleDao
    abstract fun articleRemoteKeysDao(): ArticleRemoteKeysDao
    abstract fun sourceDao(): SourceDao

    companion object{
        private var INSTANCE: NewsDb? = null
        fun getInstance(context: Context): NewsDb {
            if (INSTANCE == null) {
                synchronized(NewsDb::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NewsDb::class.java,
                        "NewsDb.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}