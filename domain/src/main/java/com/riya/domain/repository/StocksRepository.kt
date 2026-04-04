package com.riya.domain.repository

import androidx.paging.PagingData
import com.riya.domain.model.Stock
import kotlinx.coroutines.flow.Flow

interface StocksRepository {
    fun getStocks(): Flow<PagingData<Stock>>
}
