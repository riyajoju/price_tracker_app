package com.riyajoju.socket

import android.util.Log
import com.riya.domain.model.StockPriceUpdate
import com.riya.domain.repository.ConnectionState
import com.riya.domain.repository.StockSocketService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Serializable
data class SocketUpdateDto(
    val stock: String,
    val price: Double
)

@Singleton
class StockSocketServiceImpl @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json
) : StockSocketService {

    private val mutex: Mutex = Mutex()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var webSocket: WebSocket? = null
    private val subscribedSymbols = MutableStateFlow<Set<String>>(emptySet())

    private val _updates = MutableSharedFlow<StockPriceUpdate>(extraBufferCapacity = 64)
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)

    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var simulationJob: Job? = null

    init {
        startConnection()
        startSimulation()
    }

    override fun connect(): Flow<StockPriceUpdate> = _updates.asSharedFlow()

    override fun subscribeToStock(symbol: String) {
        subscribedSymbols.update { it + symbol }
    }

    override fun unsubscribeFromStock(symbol: String) {
        subscribedSymbols.update { it - symbol }
    }

    override fun startFeed() {
        startConnection()
        startSimulation()
        Log.d("SocketService", "Feed STARTED")
    }

    override fun stopFeed() {
        stopSimulation()
        closeWebSocket()
        _connectionState.value = ConnectionState.Disconnected
        Log.d("SocketService", "Feed STOPPED")
    }

    override fun disconnect() {
        stopFeed()
    }

    private fun startConnection() {
        if (webSocket != null) return

        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder()
            .url("wss://ws.postman-echo.com/raw")
            .build()


        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@StockSocketServiceImpl.webSocket = webSocket
                _connectionState.value = ConnectionState.Connected
                Log.d("SocketService", "WebSocket Connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val dto = json.decodeFromString<SocketUpdateDto>(text)
                    _updates.tryEmit(StockPriceUpdate(dto.stock, dto.price))
                } catch (e: Exception) {
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                this@StockSocketServiceImpl.webSocket = null
                _connectionState.value = ConnectionState.Disconnected

                // Auto-reconnect only if we are not manually stopped
                scope.launch {
                    delay(4000)
                    if (_connectionState.value != ConnectionState.Connected) {
                        startConnection()
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                this@StockSocketServiceImpl.webSocket = null
                _connectionState.value = ConnectionState.Disconnected
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    private fun closeWebSocket() {
        webSocket?.close(1000, "Feed stopped by user")
        webSocket = null
    }

    private fun startSimulation() {
        stopSimulation()

        simulationJob = scope.launch {
            while (isActive) {
                delay(2000)

                if (webSocket == null || subscribedSymbols.value.isEmpty()) continue

                subscribedSymbols.value.forEach { symbol ->
                    try {
                        val randomPrice = Random.nextDouble(1.0, 100.0)
                        val update = SocketUpdateDto(stock = symbol, price = randomPrice)
                        webSocket?.send(json.encodeToString(update))
                    } catch (e: Exception) {
                        Log.e("SocketService", "Simulation send error", e)
                    }
                }
            }
        }
    }

    private fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }
}