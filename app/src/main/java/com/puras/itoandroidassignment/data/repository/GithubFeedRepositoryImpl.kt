package com.puras.itoandroidassignment.data.repository

import com.puras.itoandroidassignment.data.local.dao.EntryDao
import com.puras.itoandroidassignment.data.local.dao.FeedDao
import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.data.mapper.toEntry
import com.puras.itoandroidassignment.data.mapper.toFeed
import com.puras.itoandroidassignment.data.remote.GithubApi
import com.puras.itoandroidassignment.domain.entity.EntryResponse
import com.puras.itoandroidassignment.domain.entity.FeedResponse
import com.puras.itoandroidassignment.domain.repository.GithubFeedRepository
import com.puras.itoandroidassignment.util.ErrorType
import com.puras.itoandroidassignment.util.NetworkStatusTracker
import com.puras.itoandroidassignment.util.Resource
import com.puras.itoandroidassignment.util.XMLParser
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import javax.inject.Inject

class GithubFeedRepositoryImpl @Inject constructor(
    private val api: GithubApi,
    private val okHttp: OkHttpClient,
    private val feedDao: FeedDao,
    private val entryDao: EntryDao,
    private val parser: XMLParser,
    private val networkStatusTracker: NetworkStatusTracker,
) : GithubFeedRepository {
    override suspend fun getFeeds(): Resource<List<Feed>> {
        /* Even tho network status is already checked in a viewmodel, this check here is
        *  just to make sure that the internet is available just before performing a network call */
        val result = when (networkStatusTracker.isConnected) {
            true -> {
                try {
                    val response = api.getFeed()
                    when (val code = response.code()) {
                        404 -> {
                            Timber.e("Exception in getFeeds(): 404 NOT FOUND")
                            Resource.Error(ErrorType.NOT_FOUND)
                        }

                        200 -> {
                            response.body()?.let { storeFeeds(it) }
                            Resource.Success(feedDao.get())
                        }

                        else -> {
                            Timber.e("Response code in getFeeds(): $code")
                            Resource.Error(ErrorType.UNKNOWN)
                        }
                    }
                } catch (e: Exception) {
                    Timber.e("Exception in getFeeds(): ${e.message}")
                    Resource.Error(ErrorType.UNKNOWN)
                }
            }

            else -> {
                Resource.Success(feedDao.get())
            }
        }
        return result
    }

    private suspend fun storeFeeds(body: FeedResponse) {
        val availableFeeds = mutableListOf<Feed>()
        body.links.forEach { availableFeeds.add(it.toFeed()) }
        Timber.d("Available feeds: $availableFeeds")
        feedDao.deleteAll()
        feedDao.insertList(availableFeeds)
    }

    override suspend fun getEntries(feed: Feed): Resource<List<Entry>> {
        val request = Request.Builder()
            .url(feed.link)
            .addHeader("Accept", "application/atom+xml")
            .build()
        /* Even tho network status is already checked in a viewmodel, this check here is
        *  just to make sure that the internet is available just before performing a network call */
        val result = when (networkStatusTracker.isConnected) {
            true -> {
                try {
                    val response = okHttp.newCall(request).execute()
                    when (val code = response.code) {
                        404 -> {
                            Timber.e("Response code in getEntries(): $code")
                            Resource.Error(ErrorType.NOT_FOUND)
                        }

                        200 -> {
                            val list = response.body?.let { parser(it.string()) }
                            list?.let { storeEntries(it, feed.key) }
                            Resource.Success(entryDao.get(feed.key))
                        }

                        else -> {
                            Timber.e("Response code in getEntries(): $code")
                            Resource.Error(ErrorType.UNKNOWN)
                        }
                    }
                } catch (e: Exception) {
                    Timber.e("Exception in getEntries(): ${e}")
                    Resource.Error(ErrorType.UNKNOWN)
                }
            }

            else -> {
                Resource.Success(entryDao.get(feed.key))
            }
        }
        return result
    }

    private suspend fun storeEntries(list: List<EntryResponse>, feedKey: String) {
        val entries = mutableListOf<Entry>()
        list.forEach { entries.add(it.toEntry(feedKey)) }
        entryDao.delete(feedKey)
        entryDao.insertList(entries)
    }
}
