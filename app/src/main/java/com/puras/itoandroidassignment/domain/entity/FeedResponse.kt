package com.puras.itoandroidassignment.domain.entity

import com.google.gson.annotations.SerializedName

data class FeedResponse(
    @SerializedName("repository_discussions_category_url") val repository_discussions_category_url: String,
    @SerializedName("repository_discussions_url") val repository_discussions_url: String,
    @SerializedName("security_advisories_url") val security_advisories_url: String,
    @SerializedName("timeline_url") val timeline_url: String,
    @SerializedName("user_url") val user_url: String,
    @SerializedName("_links") val links: Map<String, LinkResponse>,
)

data class LinkResponse(
    @SerializedName("href") val href: String
)