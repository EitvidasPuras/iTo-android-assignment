package com.puras.itoandroidassignment.domain.use_case

import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.domain.repository.GithubFeedRepository
import com.puras.itoandroidassignment.util.Resource
import javax.inject.Inject

interface GetFeedsUseCase {
    suspend operator fun invoke(): Resource<List<Feed>>
}

class GetFeedsUseCaseImpl @Inject constructor(
    private val repository: GithubFeedRepository,
) : GetFeedsUseCase {
    override suspend operator fun invoke(): Resource<List<Feed>> {
        return repository.getFeed()
    }
}