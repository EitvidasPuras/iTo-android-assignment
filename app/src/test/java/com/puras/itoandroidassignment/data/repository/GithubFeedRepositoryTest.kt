package com.puras.itoandroidassignment.data.repository

import com.puras.itoandroidassignment.data.local.dao.EntryDao
import com.puras.itoandroidassignment.data.local.dao.FeedDao
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.data.remote.GithubApi
import com.puras.itoandroidassignment.domain.entity.FeedResponse
import com.puras.itoandroidassignment.domain.entity.LinkResponse
import com.puras.itoandroidassignment.domain.repository.GithubFeedRepository
import com.puras.itoandroidassignment.util.ErrorType
import com.puras.itoandroidassignment.util.NetworkStatusTracker
import com.puras.itoandroidassignment.util.Resource
import com.puras.itoandroidassignment.util.XMLParser
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException

class GithubFeedRepositoryTest {

    private val api = mockk<GithubApi>()
    private val feedDao = mockk<FeedDao>(relaxed = true)

    private val okHttp = mockk<OkHttpClient>(relaxed = true)
    private val entryDao = mockk<EntryDao>()
    private val parser = mockk<XMLParser>(relaxed = true)

    private val networkStatusTracker = mockk<NetworkStatusTracker>()

    private lateinit var repository: GithubFeedRepository
    private lateinit var request: Request

    @Before
    fun setup() {
        repository = GithubFeedRepositoryImpl(
            api, okHttp, feedDao, entryDao, parser, networkStatusTracker
        )
        request = Request.Builder()
            .url("https://github.com/timeline")
            .addHeader("Accept", "application/atom+xml")
            .build()
    }

    @Test
    fun `get feeds should return not found error when internet is available`() {
        coEvery { api.getFeed().code() } returns 404
        coEvery { networkStatusTracker.isConnected } returns true

        val result = runBlocking { repository.getFeeds() }
        assertEquals(Resource.Error<List<Feed>>(errorType = ErrorType.NOT_FOUND), result)
    }

    @Test
    fun `get feeds should return unknown error when internet is available`() {
        coEvery { api.getFeed().code() } returns 500
        coEvery { networkStatusTracker.isConnected } returns true

        val result = runBlocking { repository.getFeeds() }
        assertEquals(Resource.Error<List<Feed>>(errorType = ErrorType.UNKNOWN), result)
    }

    @Test
    fun `get feeds should return unknown error if SocketTimeoutException is thrown`() {
        coEvery { api.getFeed() } throws SocketTimeoutException()
        coEvery { networkStatusTracker.isConnected } returns true

        val result = runBlocking { repository.getFeeds() }
        assertEquals(Resource.Error<List<Feed>>(errorType = ErrorType.UNKNOWN), result)
    }

    @Test
    fun `get feeds should return empty Success object if no internet and database is empty`() {
        coEvery { networkStatusTracker.isConnected } returns false
        coEvery { feedDao.get() } returns emptyList()

        val result = runBlocking { repository.getFeeds() }
        assertEquals(Resource.Success<List<Feed>>(data = emptyList()), result)
    }

    @Test
    fun `get feeds should return non-empty Success object if no internet and database is not empty`() {
        coEvery { networkStatusTracker.isConnected } returns false
        coEvery { feedDao.get() } returns listOfFixedFeeds

        val result = runBlocking { repository.getFeeds() }
        assertEquals(Resource.Success<List<Feed>>(data = listOfFixedFeeds), result)
    }

    @Test
    fun `get feeds should return Feed when api response is correct`() {
        coEvery { networkStatusTracker.isConnected } returns true
        coEvery { api.getFeed().code() } returns 200
        coEvery { api.getFeed().body() } returns feedResponse
        coEvery { feedDao.get() } returns listOfFixedFeeds

        val result = runBlocking { repository.getFeeds() }

        coVerifyAll {
            feedDao.deleteAll()
            feedDao.insertList(any())
            feedDao.get()
        }
        assertEquals(Resource.Success<List<Feed>>(data = listOfFixedFeeds), result)
    }

    companion object {
        private val listOfFixedFeeds = listOf(
            Feed(
                id = 0,
                key = "timeline",
                link = "https://github.com/timeline"
            ),
            Feed(
                id = 1,
                key = "user",
                link = "https://github.com/{user}"
            ),
            Feed(
                id = 2,
                key = "repository_discussions",
                link = "https://github.com/{user}/{repo}/discussions"
            ),
            Feed(
                id = 3,
                key = "repository_discussions_category",
                link = "https://github.com/{user}/{repo}/discussions/categories/{category}"
            ),
            Feed(
                id = 4,
                key = "security_advisories",
                link = "https://github.com/security-advisories"
            )
        )
        private val map = mapOf(
            "timeline" to LinkResponse(href = "https://github.com/timeline"),
            "user" to LinkResponse(href = "https://github.com/{user}"),
            "repository_discussions" to LinkResponse(href = "https://github.com/{user}/{repo}/discussions"),
            "repository_discussions_category" to LinkResponse(href = "https://github.com/{user}/{repo}/discussions/categories/{category}"),
            "security_advisories" to LinkResponse(href = "https://github.com/security-advisories")
        )
        private val feedResponse = FeedResponse(
            timeline_url = "https://github.com/timeline",
            user_url = "https://github.com/{user}",
            repository_discussions_url = "https://github.com/{user}/{repo}/discussions",
            repository_discussions_category_url = "https://github.com/{user}/{repo}/discussions/categories/{category}",
            security_advisories_url = "https://github.com/security-advisories",
            links = map
        )
    }
}