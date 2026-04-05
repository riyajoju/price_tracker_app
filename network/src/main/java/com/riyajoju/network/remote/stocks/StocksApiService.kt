package com.riyajoju.network.remote.stocks

import com.riyajoju.network.remote.stocks.dto.response.StockResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StocksApiService {
    @GET("stocks")
    suspend fun getStocks(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<StockResponseDto>
}