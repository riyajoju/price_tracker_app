package com.riya.domain.repository

import com.riya.domain.model.Stock
import com.riya.domain.result_handling.Result

interface StocksRepository {
    suspend fun getStocks(page: Int, limit: Int): Result<List<Stock>>
}