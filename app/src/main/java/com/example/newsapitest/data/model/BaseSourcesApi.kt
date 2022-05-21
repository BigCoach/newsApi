package com.example.newsapitest.data.model

import com.google.gson.annotations.SerializedName

open class BaseSourcesApi<T>{
    @SerializedName("sources")
    var sources: T? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("code")
    var errorCode: String? = null

    @SerializedName("message")
    var errorMessage: String? = null
}