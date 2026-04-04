package com.riya.home.viewmodel

import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riya.domain.model.Stock
import com.riya.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.riya.domain.result_handling.Result
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val stocks: List<Stock>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StocksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchStocks()
    }

    private fun fetchStocks() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            when (val result = repository.getStocks(1, 20)) {
                is Result.Success -> {
                    Log.d("HomeViewModel", "Stocks fetched: ${result.data}")
                    _uiState.value = HomeUiState.Success(result.data)
                }

                is Result.Error -> {
                    Log.e("HomeViewModel", "Error fetching stocks: ${result.exception}")
                    _uiState.value = HomeUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
}