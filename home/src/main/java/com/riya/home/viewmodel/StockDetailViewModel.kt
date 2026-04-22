package com.riya.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riya.domain.repository.StockSocketService
import com.riya.domain.usecase.GetStockUseCase
import com.riya.domain.usecase.ManageSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val getStockPriceUseCase: GetStockUseCase,
    private val manageSubscriptionUseCase: ManageSubscriptionUseCase,
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
            getStockPriceUseCase(symbol).collect { update ->
                _livePrice.value = update.price
            }
        }

        // 3. Start simulation for this stock
        manageSubscriptionUseCase.subscribe(symbol)
    }
}
