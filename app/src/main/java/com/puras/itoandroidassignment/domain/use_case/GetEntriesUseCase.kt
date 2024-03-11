package com.puras.itoandroidassignment.domain.use_case

import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.domain.repository.GithubFeedRepository
import com.puras.itoandroidassignment.util.Resource
import javax.inject.Inject

interface GetEntriesUseCase {
    suspend operator fun invoke(feed: Feed): Resource<List<Entry>>
}

class GetEntriesUseCaseImpl @Inject constructor(
    private val repository: GithubFeedRepository,
) : GetEntriesUseCase {
    override suspend fun invoke(feed: Feed): Resource<List<Entry>> {
        return repository.getEntries(feed)
    }
}