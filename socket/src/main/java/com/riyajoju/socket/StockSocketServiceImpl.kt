package com.riyajoju.socket

import android.util.Log
import com.riya.domain.model.StockPriceUpdate
import com.riya.domain.repository.StockSocketService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Serializable
data class SocketUpdateDto(
    val s: String,
    val p: Double
)

@Singleton
class StockSocketServiceImpl @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json
) : StockSocketService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var webSocket: WebSocket? = null
    private val subscribedSymbols = MutableStateFlow<Set<String>>(emptySet())

    private val _updates = MutableSharedFlow<StockPriceUpdate>(extraBufferCapacity = 64)

    init {
        startConnection()
        startSimulation()
    }

    private fun startConnection() {

        val request = Request.Builder()
            .url("wss://ws.postman-echo.com/raw")
            .build()

        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val dto = json.decodeFromString<SocketUpdateDto>(text)
                    _updates.tryEmit(StockPriceUpdate(dto.s, dto.p))
                } catch (e: Exception) {
                    // Ignore malformed echo back
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("SocketService", "Socket failure, reconnecting in 5s: ${t.message}")
                scope.launch {
                    delay(5000)
                    startConnection()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("SocketService", "Socket closed: $reason")
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    private fun startSimulation() {
        scope.launch {
            while (isActive) {
                delay(2000)
                val currentSymbols = subscribedSymbols.value
                val currentSocket = webSocket

                if (currentSocket != null && currentSymbols.isNotEmpty()) {
                    currentSymbols.forEach { symbol ->
                        try {
                            val randomPrice = Random.nextDouble(100.0, 1000.0)
                            val update = SocketUpdateDto(s = symbol, p = randomPrice)
                            val jsonString = json.encodeToString(update)
                            currentSocket.send(jsonString)
                        } catch (e: Exception) {
                            Log.e("SocketService", "Simulation send error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    override fun connect(): Flow<StockPriceUpdate> = _updates.asSharedFlow()

    override fun subscribeToStock(symbol: String) {
        subscribedSymbols.update { it + symbol }
    }

    override fun unsubscribeFromStock(symbol: String) {
        subscribedSymbols.update { it - symbol }
    }

    override fun disconnect() {
        // In a singleton, we might not want to fully disconnect often
        // but we can clear subscriptions
        subscribedSymbols.value = emptySet()
    }
}
