package com.riya.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.riya.domain.model.Stock
import com.riya.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StocksRepository
) : ViewModel() {

    val stocks: Flow<PagingData<Stock>> = repository.getStocks()
        .cachedIn(viewModelScope)
}
