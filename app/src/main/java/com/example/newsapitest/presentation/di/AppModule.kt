package com.example.newsapitest.presentation.di

import android.content.Context
import androidx.room.Room
import com.example.newsapitest.data.db.ArticleDao
import com.example.newsapitest.data.db.ArticleRemoteKeysDao
import com.example.newsapitest.data.db.NewsDb
import com.example.newsapitest.data.db.SourceDao
import com.example.newsapitest.data.network.NewsApiService
import com.example.newsapitest.data.repository.NewsRepoImpl
import com.example.newsapitest.domain.repository.NewsApiRepo
import com.example.newsapitest.util.Constants.Companion.BASE_URL
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun retrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideBaseOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()
    }

    @Provides
    @Singleton
    fun newsApiService() : NewsApiService {
        return retrofit().create(NewsApiService::class.java)
    }

    @Provides
    fun provideBaseOkHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
        httpClientBuilder.readTimeout(5, TimeUnit.MINUTES)
        httpClientBuilder.writeTimeout(5, TimeUnit.MINUTES)
        httpClientBuilder.addInterceptor(httpLoggingInterceptor())
        return httpClientBuilder.build()
    }

    @Singleton
    @Provides
    fun gson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Singleton
    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor{
        val loggingInterceptor = HttpLoggingInterceptor { message -> Timber.d(message) }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context : Context) =
        Room.databaseBuilder(context, NewsDb::class.java, "NewsDb.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideArticleDAO(appDatabase: NewsDb): ArticleDao {
        return appDatabase.articlesDao()
    }

    @Provides
    fun provideSourceDAO(appDatabase: NewsDb): SourceDao {
        return appDatabase.sourceDao()
    }

    @Provides
    fun provideArticleRemoteKeysDao(appDatabase: NewsDb): ArticleRemoteKeysDao {
        return appDatabase.articleRemoteKeysDao()
    }

}