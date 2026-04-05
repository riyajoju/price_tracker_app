package com.riyajoju.socket.di

import com.riya.domain.repository.StockSocketService
import com.riyajoju.socket.StockSocketServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocketModule {

    @Binds
    @Singleton
    abstract fun bindStockSocketService(
        impl: StockSocketServiceImpl
    ): StockSocketService
}
