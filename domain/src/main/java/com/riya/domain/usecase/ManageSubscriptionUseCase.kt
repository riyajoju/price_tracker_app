package com.riya.domain.usecase

import com.riya.domain.repository.StockSocketService
import javax.inject.Inject

// In :domain module
class ManageSubscriptionUseCase @Inject constructor(
    private val socketService: StockSocketService
) {
    fun subscribe(symbol: String) = socketService.subscribeToStock(symbol)
    fun unsubscribe(symbol: String) = socketService.unsubscribeFromStock(symbol)
}