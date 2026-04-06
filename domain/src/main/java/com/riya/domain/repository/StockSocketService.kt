package com.riya.domain.repository

import com.riya.domain.model.StockPriceUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
}

interface StockSocketService {
    fun connect(): Flow<StockPriceUpdate>
    fun subscribeToStock(symbol: String)
    fun unsubscribeFromStock(symbol: String)
    fun disconnect()
    fun startFeed()
    fun stopFeed()
    val connectionState: StateFlow<ConnectionState>
}
