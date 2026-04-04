package com.riya.domain.model

data class Stock(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val percent: Double,
    val logo: String
)