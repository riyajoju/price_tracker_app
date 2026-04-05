package com.riya.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riya.domain.repository.StockSocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val socketService: StockSocketService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Retrieve symbol from Navigation arguments
    private val symbol: String = checkNotNull(savedStateHandle["symbol"])
    private val initialPrice: Double = checkNotNull(savedStateHandle["price"])

    private val _livePrice = MutableStateFlow(initialPrice)
    val livePrice = _livePrice.asStateFlow()

    init {
        observeLiveUpdates()
    }

    private fun observeLiveUpdates() {
        viewModelScope.launch {
            // 1. Connect to the socket
            socketService.connect().collect { update ->
                // 2. Filter for the current stock symbol
                if (update.symbol == symbol) {
                    _livePrice.value = update.price
                }
            }
        }
        
        // 3. Start simulation for this stock
        socketService.subscribeToStock(symbol)
    }

    override fun onCleared() {
        super.onCleared()
        // 4. Stop updates and disconnect when leaving the screen
        socketService.unsubscribeFromStock(symbol)
        socketService.disconnect()
    }
}
