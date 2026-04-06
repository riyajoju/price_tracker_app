package com.riya.home.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.riya.designsystem.theme.AppColors
import com.riya.home.viewmodel.StockDetailViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    name: String,
    symbol: String,
    basePrice: Double,
    viewModel: StockDetailViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    onBackClick: () -> Unit
) {
    val livePrice by viewModel.livePrice.collectAsState()

    // 2. Logic for Color and Percentage
    val isPositive = livePrice >= basePrice
    val trendColor = if (isPositive) AppColors.SuccessGreen else AppColors.ErrorRed

    val diffPercent = remember(livePrice, basePrice) {
        val diff = livePrice - basePrice
        (diff / basePrice) * 100
    }

    // 3. Flash effect logic (Fixed to flash when price actually changes)
    var isFlashing by remember { mutableStateOf(false) }
    LaunchedEffect(livePrice) {
        isFlashing = true
        delay(300)
        isFlashing = false
    }

    // Animate between the trend color (Green/Red) and a lighter version when flashing
    val animatedPriceColor by animateColorAsState(
        targetValue = if (isFlashing) trendColor.copy(alpha = 0.5f) else trendColor,
        animationSpec = tween(durationMillis = 300),
        label = "PriceFlash"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "$name ($symbol)") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Large Live Price
            Text(
                text = "$${String.format(Locale.US, "%.2f", livePrice)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = animatedPriceColor
            )

            // 5. Percentage Change Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = String.format(Locale.US, "%.2f%%", abs(diffPercent)),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = trendColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "${name}. is a leading global technology company headquartered in Cupertino, California. The company designs, manufactures, and markets consumer electronics, software, and online services, including the iPhone, Mac computers, iPad, Apple Watch, and services such as Apple Music, iCloud, and the App Store. With a strong ecosystem and loyal customer base, Apple continues to drive innovation in premium hardware and digital services.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )

        }
    }
}