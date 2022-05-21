package com.example.newsapitest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArticleRemoteKeys (
    @PrimaryKey
    val publishedAt: String,
    val nextPage: Int?,
    val previousPage: Int?
)
