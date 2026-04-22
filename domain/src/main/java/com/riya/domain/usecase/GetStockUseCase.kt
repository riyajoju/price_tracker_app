package com.riya.domain.usecase

import com.riya.domain.model.StockPriceUpdate
import com.riya.domain.repository.StockSocketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

class GetStockUseCase @Inject constructor(
    val stockSocketService: StockSocketService
) {
    operator fun invoke(symbol: String): Flow<StockPriceUpdate> {
        return stockSocketService.connect()
            .filter { it.symbol == symbol }
    }
}