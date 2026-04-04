package com.riyajoju.network.remote.stocks

import com.riya.domain.result_handling.Result
import com.riyajoju.network.common.NetworkHandler
import com.riyajoju.network.remote.stocks.dto.response.StockResponseDto
import javax.inject.Inject

class StocksRemoteDataSource @Inject constructor(
    private val apiService: StocksApiService
) {
    suspend fun getStocks(page: Int, limit: Int): Result<StockResponseDto> {
        return NetworkHandler.safeApiCall {
            apiService.getStocks(page, limit)
        }
    }
}