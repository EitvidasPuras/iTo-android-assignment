package com.puras.itoandroidassignment.di

import com.puras.itoandroidassignment.domain.use_case.GetEntriesUseCase
import com.puras.itoandroidassignment.domain.use_case.GetEntriesUseCaseImpl
import com.puras.itoandroidassignment.domain.use_case.GetFeedsUseCase
import com.puras.itoandroidassignment.domain.use_case.GetFeedsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    @Singleton
    abstract fun bindGetFeedsUseCase(
        getFeedsUseCase: GetFeedsUseCaseImpl
    ): GetFeedsUseCase

    @Binds
    @Singleton
    abstract fun bindGetPublicTimelineUseCase(
        getPublicTimelineUseCase: GetEntriesUseCaseImpl
    ): GetEntriesUseCase
}