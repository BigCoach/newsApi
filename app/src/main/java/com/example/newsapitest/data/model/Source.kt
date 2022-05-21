package com.example.newsapitest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Source(
    @PrimaryKey
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String?,
    var isSelected: Boolean = false
)