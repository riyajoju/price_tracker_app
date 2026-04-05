package com.riya.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.riya.domain.model.StockPriceUpdate
import com.riya.domain.repository.StockSocketService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StockDetailViewModelTest {

    private val socketService = mockk<StockSocketService>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    
    private val socketFlow = MutableSharedFlow<StockPriceUpdate>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { socketService.connect() } returns socketFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should subscribe to stock and show initial price`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle(
            mapOf("symbol" to "AAPL", "price" to 150.0)
        )

        // When
        val viewModel = StockDetailViewModel(socketService, savedStateHandle)
        runCurrent() // Allow init block to run

        // Then
        assertEquals(150.0, viewModel.livePrice.value, 0.0)
        verify { socketService.subscribeToStock("AAPL") }
    }

    @Test
    fun `matching socket update should update livePrice`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle(
            mapOf("symbol" to "AAPL", "price" to 150.0)
        )
        val viewModel = StockDetailViewModel(socketService, savedStateHandle)
        runCurrent()

        viewModel.livePrice.test {
            assertEquals(150.0, awaitItem(), 0.0) // Initial item

            // When
            socketFlow.emit(StockPriceUpdate("AAPL", 155.5))
            runCurrent()

            // Then
            assertEquals(155.5, awaitItem(), 0.0)
        }
    }

    @Test
    fun `non-matching socket update should be ignored`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle(
            mapOf("symbol" to "AAPL", "price" to 150.0)
        )
        val viewModel = StockDetailViewModel(socketService, savedStateHandle)
        runCurrent()

        viewModel.livePrice.test {
            assertEquals(150.0, awaitItem(), 0.0)

            // When: Emit update for a different symbol
            socketFlow.emit(StockPriceUpdate("GOOGL", 2800.0))
            runCurrent()

            // Then: No new item should be emitted (ensure it doesn't time out)
            expectNoEvents() 
        }
    }
}
