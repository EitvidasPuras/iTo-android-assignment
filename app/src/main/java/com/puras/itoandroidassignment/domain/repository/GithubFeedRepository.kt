package com.puras.itoandroidassignment.domain.repository

import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.util.Resource

interface GithubFeedRepository {
    suspend fun getFeed(): Resource<List<Feed>>
}