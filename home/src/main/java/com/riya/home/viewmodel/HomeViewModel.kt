package com.riya.home.viewmodel

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riya.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.riya.domain.result_handling.Result

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StocksRepository
): ViewModel() {

    init {

    }

    private fun fetchStocks() {
        viewModelScope.launch {
            when (val result = repository.getStocks(1, 20)) {
                is Result.Success -> {
                    // Handle success
                }
                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }
}