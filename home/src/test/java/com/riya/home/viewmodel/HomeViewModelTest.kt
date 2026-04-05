package com.riya.home.viewmodel

import app.cash.turbine.test
import com.riya.domain.model.StockPriceUpdate
import com.riya.domain.repository.StockSocketService
import com.riya.domain.repository.StocksRepository
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

    private val repository = mockk<StocksRepository>(relaxed = true)
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
    fun `subscribeToStock should call socketService subscribeToStock`() {
        // Mock socket connection to return an empty flow
        every { socketService.connect() } returns flowOf()
        val viewModel = HomeViewModel(repository, socketService)

        // Given
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
        // Ensure the flow emits the update
        every { socketService.connect() } returns flowOf(update)
        
        // When: Initialize ViewModel
        val vm = HomeViewModel(repository, socketService)
        
        // Crucial: Advance the dispatcher so the 'init' block's coroutine can run
        advanceUntilIdle() 
        
        // Then: Verify the state
        vm.stockPrices.test {
            val result = awaitItem()
            assertEquals(150.0, result["AAPL"])
        }
    }
}
