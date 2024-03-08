package com.puras.itoandroidassignment.di

import com.puras.itoandroidassignment.data.repository.GithubFeedRepositoryImpl
import com.puras.itoandroidassignment.domain.repository.GithubFeedRepository
import com.puras.itoandroidassignment.domain.use_case.GetFeedsUseCase
import com.puras.itoandroidassignment.domain.use_case.GetFeedsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGithubFeedRepo(
        githubFeedRepoImpl: GithubFeedRepositoryImpl
    ): GithubFeedRepository

    @Binds
    @Singleton
    abstract fun bindGetFeedsUseCase(
        getFeedsUseCase: GetFeedsUseCaseImpl
    ): GetFeedsUseCase
}