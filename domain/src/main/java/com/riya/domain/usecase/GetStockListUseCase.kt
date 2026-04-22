package com.riya.domain.usecase

import androidx.paging.PagingData
import com.riya.domain.model.Stock
import com.riya.domain.repository.StocksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStockListUseCase @Inject constructor(
    private val repository: StocksRepository
) {
    operator fun invoke(): Flow<PagingData<Stock>> = repository.getStocks()
}