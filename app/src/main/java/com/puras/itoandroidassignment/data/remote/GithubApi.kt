package com.puras.itoandroidassignment.data.remote

import com.puras.itoandroidassignment.domain.entity.FeedResponse
import retrofit2.Response
import retrofit2.http.GET

interface GithubApi {

    @GET("/feeds")
    suspend fun getFeed(): Response<FeedResponse>
}