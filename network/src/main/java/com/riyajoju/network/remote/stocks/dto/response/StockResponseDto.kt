package com.riyajoju.network.remote.stocks.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockResponseDto(
    @SerialName("page") val page: Int,
    val limit: Int,
    val total: Int,
    val hasNext: Boolean,
    val stocks: List<StockDto>
)

@Serializable
data class StockDto(
    val symbol: String,
    val displayName: String,
    val price: Double,
    val change: Double,
    val percent: Double,
    val logoUrl: String
)
