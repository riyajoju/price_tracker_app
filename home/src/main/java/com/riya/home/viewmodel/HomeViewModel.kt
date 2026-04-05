package com.riya.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.riya.domain.model.Stock
import com.riya.domain.repository.StockSocketService
import com.riya.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StocksRepository,
    private val socketService: StockSocketService
) : ViewModel() {

    private val _stockPriceUpdates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val stockPrices = _stockPriceUpdates.asStateFlow()

    val stocks: Flow<PagingData<Stock>> = repository.getStocks()
        .cachedIn(viewModelScope)

    init {
        observeSocketPriceUpdates()
    }

    private fun observeSocketPriceUpdates() {
        viewModelScope.launch {
            socketService.connect().collect { update ->
                _stockPriceUpdates.update { currentMap ->
                    currentMap + (update.symbol to update.price)
                }
            }
        }
    }

    fun subscribeToStock(symbol: String) {
        socketService.subscribeToStock(symbol)
    }
}
