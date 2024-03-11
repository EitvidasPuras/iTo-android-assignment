package com.puras.itoandroidassignment.domain.repository

import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.util.Resource

interface GithubFeedRepository {
    suspend fun getFeeds(): Resource<List<Feed>>
    suspend fun getEntries(feed: Feed): Resource<List<Entry>>
}