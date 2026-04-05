package com.riya.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.riya.domain.model.Stock
import com.riya.home.viewmodel.HomeViewModel
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onStockClick: (Stock) -> Unit
) {

    val stocks = viewModel.stocks.collectAsLazyPagingItems()
    val livePrices by viewModel.stockPrices.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stocks") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StockList(stocks = stocks, viewModel, livePrices, onStockClick)

            // Show global loading indicator
            if (stocks.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Show initial load error
            if (stocks.loadState.refresh is LoadState.Error) {
                val error = stocks.loadState.refresh as LoadState.Error
                Text(
                    text = "Error: ${error.error.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun StockList(
    stocks: LazyPagingItems<Stock>,
    viewModel: HomeViewModel,
    livePrices: Map<String, Double>,
    onStockClick: (Stock) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = stocks.itemCount,
            key = stocks.itemKey { it.symbol },
            contentType = stocks.itemContentType { "stocks" }
        ) { index ->
            stocks[index]?.let { stock ->
                LaunchedEffect(stock.symbol) {
                    viewModel.subscribeToStock(stock.symbol)
                }
                val currentPrice = livePrices[stock.symbol] ?: stock.price
                StockCard(stock, currentPrice, onStockClick)
            }
        }

        // Show loading indicator at the bottom when loading next page
        if (stocks.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun StockCard(stock: Stock, currentPrice: Double, onStockClick: (Stock) -> Unit) {

    val isPositive = currentPrice >= stock.price
    val priceColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
    val formattedPrice = remember(currentPrice) {
        String.format("%.2f", currentPrice, Locale.US)
    }

    val diffPercent = remember(currentPrice, stock.price) {
        val diff = currentPrice - stock.price
        (diff / stock.price) * 100
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onStockClick(stock) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(stock.logo)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.Warning)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stock.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = stock.symbol, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${formattedPrice}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    color = priceColor
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = priceColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        // Use abs() so we don't show "Down Arrow -1.50%" (Double negatives)
                        text = String.format(Locale.US, "%.2f%%", abs(diffPercent)),
                        style = MaterialTheme.typography.labelSmall,
                        color = priceColor
                    )
                }
            }
        }
    }
}
