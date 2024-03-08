package com.puras.itoandroidassignment.domain.entity

import com.google.gson.annotations.SerializedName

data class LinkResponse(
    @SerializedName("href") val href: String
)