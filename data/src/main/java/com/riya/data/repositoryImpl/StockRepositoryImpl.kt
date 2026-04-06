package com.riya.data.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.riya.data.paging.StockPagingSource
import com.riya.domain.model.Stock
import com.riya.domain.repository.StocksRepository
import com.riyajoju.network.remote.stocks.StocksRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StocksRepositoryImpl @Inject constructor(
    private val remoteDataSource: StocksRemoteDataSource
) : StocksRepository {

    override fun getStocks(): Flow<PagingData<Stock>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StockPagingSource(remoteDataSource)
            }
        ).flow
    }
}
