package com.riya.domain.repository

import com.riya.domain.model.StockPriceUpdate
import kotlinx.coroutines.flow.Flow

interface StockSocketService {
    fun connect(): Flow<StockPriceUpdate>
    fun subscribeToStock(symbol: String)
    fun unsubscribeFromStock(symbol: String)
    fun disconnect()
}
