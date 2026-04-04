package com.riya.data.di

import com.riya.data.repositoryImpl.StocksRepositoryImpl
import com.riya.domain.repository.StocksRepository
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
    abstract fun bindStocksRepository(
        stocksRepositoryImpl: StocksRepositoryImpl
    ): StocksRepository
}
