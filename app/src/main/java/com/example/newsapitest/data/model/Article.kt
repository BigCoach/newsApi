package com.example.newsapitest.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(indices = [Index(value = ["title", "description"], unique = true)])
data class Article(
    @PrimaryKey(autoGenerate = true)
    var localId: Long,
    @SerializedName("author")
    var author: String?,
    @SerializedName("title")
    var title: String,
    @SerializedName("description")
    var description: String?,
    @SerializedName("url")
    var url: String?,
    @SerializedName("urlToImage")
    var urlToImage: String?,
    @SerializedName("publishedAt")
    var publishedAt: String?,
    @SerializedName("content")
    var content: String?,
    var inFavorites: Boolean = false,
    var sourceId: String?,
    var sourceName: String?,
){
    override fun toString(): String {
        return "Article(localId=$localId, title=$title)"
    }
}
