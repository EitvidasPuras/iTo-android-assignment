package com.puras.itoandroidassignment.data.repository

import com.puras.itoandroidassignment.data.local.dao.FeedDao
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.data.remote.GithubApi
import com.puras.itoandroidassignment.domain.entity.FeedResponse
import com.puras.itoandroidassignment.domain.repository.GithubFeedRepository
import com.puras.itoandroidassignment.util.IsNetworkAvailable
import com.puras.itoandroidassignment.util.Resource
import kotlinx.coroutines.flow.last
import timber.log.Timber
import javax.inject.Inject

class GithubFeedRepositoryImpl @Inject constructor(
    private val api: GithubApi,
    private val dao: FeedDao,
    private val isNetworkAvailable: IsNetworkAvailable,
) : GithubFeedRepository {
    override suspend fun getFeed(): Resource<List<Feed>> {
        val response = when (isNetworkAvailable().last()) {
            true -> {
                try {
                    val body = api.getFeed().body()
                    body?.let { storeFeedUrls(it) }
                    Resource.Success(dao.getFeeds())
                } catch (e: Exception) {
                    Timber.e("Exception in getFeed(): ${e.message}")
                    Resource.Error(e.message.toString())
                }
            }

            else -> {
                Resource.Success(dao.getFeeds())
            }
        }
        return response
    }

    private suspend fun storeFeedUrls(body: FeedResponse) {
        val availableFeeds = mutableListOf<Feed>()
        body.links.forEach {
            availableFeeds.add(Feed(link = it.value.href))
        }
        Timber.e("$availableFeeds")
        dao.deleteAll()
        dao.insertList(availableFeeds)
    }
}