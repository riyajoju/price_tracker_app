package com.riya.home.viewmodel

import app.cash.turbine.test
import com.riya.domain.model.StockPriceUpdate
import com.riya.domain.repository.StockSocketService
import com.riya.domain.usecase.GetStockListUseCase
import com.riya.domain.usecase.TogglepriceFeedUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val getStockListUseCase = mockk<GetStockListUseCase>(relaxed = true)
    private val togglePriceFeedUseCase = mockk<TogglepriceFeedUseCase>(relaxed = true)
    private val socketService = mockk<StockSocketService>(relaxed = true)
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `togglePriceFeed should call togglePriceFeedUseCase`() {
        // Given
        val viewModel = HomeViewModel(getStockListUseCase, togglePriceFeedUseCase, socketService)

        // When
        viewModel.togglePriceFeed()

        // Then
        verify { togglePriceFeedUseCase() }
    }

    @Test
    fun `subscribeToStock should call socketService subscribeToStock`() {
        // Given
        val viewModel = HomeViewModel(getStockListUseCase, togglePriceFeedUseCase, socketService)
        val symbol = "TSLA"

        // When
        viewModel.subscribeToStock(symbol)

        // Then
        verify { socketService.subscribeToStock(symbol) }
    }

    @Test
    fun `socket price update should update stockPrices state`() = runTest {
        // Given
        val update = StockPriceUpdate("AAPL", 150.0)
        every { socketService.connect() } returns flowOf(update)
        
        // When: Initialize ViewModel
        val vm = HomeViewModel(getStockListUseCase, togglePriceFeedUseCase, socketService)
        
        // Advance dispatcher to let init block run
        advanceUntilIdle() 
        
        // Then: Verify the state
        vm.stockPrices.test {
            val result = awaitItem()
            assertEquals(150.0, result["AAPL"])
        }
    }
}
