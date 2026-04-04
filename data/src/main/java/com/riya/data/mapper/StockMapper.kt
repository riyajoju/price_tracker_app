package com.riya.data.mapper

import com.riya.domain.model.Stock
import com.riyajoju.network.remote.stocks.dto.response.StockDto

fun StockDto.toDomain(): Stock {
    return Stock(
        symbol = symbol,
        name = displayName,
        price = price,
        change = change,
        percent = percent,
        logo = logoUrl
    )
}