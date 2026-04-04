package com.riyajoju.pricetracker.repositoryImplimport

import com.riya.domain.model.Stock

com.riya.domain.model.Stock
import com.riya.domain.repository.StocksRepository
import com.riya.domain.result_handling.Result
import com.riyajoju.network.remote.stocks.StocksRemoteDataSource
import com.riyajoju.pricetracker.mapper.toDomain
import javax.inject.Inject

class StocksRepositoryImpl @Inject constructor(
    private val remoteDataSource: StocksRemoteDataSource
) : StocksRepository {

    override suspend fun getStocks(
        page: Int,
        limit: Int
    ): Result<List<Stock>> {
        // Use the remote data source to fetch data
        val response = remoteDataSource.getStocks(page, limit)

        return when (response) {
            is Result.Success -> {
                // Map the Network DTO to the Domain Model
                Result.Success(
                    response.data.stocks.map { it.toDomain() }
                )
            }

            is Result.Error -> {
                // Pass through the exception
                Result.Error(response.exception)
            }
        }
    }
}