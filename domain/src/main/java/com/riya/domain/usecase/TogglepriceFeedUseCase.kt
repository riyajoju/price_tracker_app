package com.riya.domain.usecase

import com.riya.domain.repository.ConnectionState
import com.riya.domain.repository.StockSocketService
import javax.inject.Inject

class TogglepriceFeedUseCase @Inject constructor(
    private val stockSocketService: StockSocketService
) {
    operator fun invoke() {
        val currentState = stockSocketService.connectionState.value
        if (currentState is ConnectionState.Connected) {
            stockSocketService.stopFeed()
        } else {
            stockSocketService.startFeed()
        }
    }
}