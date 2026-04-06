package com.riyajoju.pricetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.riyajoju.pricetracker.ui.theme.PriceTrackerAppTheme
import com.riya.home.view.HomeScreen
import com.riya.home.view.StockDetailScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object WatchlistRoute

@Serializable
object PortfolioRoute

@Serializable
data class StockDetailRoute(
    val symbol: String,
    val name: String,
    val price: Double
)

data class BottomNavItem<T : Any>(
    val name: String,
    val route: T,
    val icon: ImageVector
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PriceTrackerAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("Explore", HomeRoute, Icons.Default.Search),
        BottomNavItem("Watchlist", WatchlistRoute, Icons.Default.Favorite),
        BottomNavItem("Portfolio", PortfolioRoute, Icons.Default.AccountBalanceWallet)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottomBar(navController, items)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeRoute> {
                HomeScreen(onStockClick = { stock ->
                    navController.navigate(
                        StockDetailRoute(
                            symbol = stock.symbol,
                            name = stock.name,
                            price = stock.price
                        )
                    )
                })
            }
            composable<WatchlistRoute> {
                Text(
                    "Watchlist Screen",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable<PortfolioRoute> {
                Text(
                    "Portfolio Screen",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable<StockDetailRoute>(
                deepLinks = listOf(
                    navDeepLink<StockDetailRoute>(
                        basePath = "stocks://symbol"
                    )
                )
            ) { backStackEntry ->
                val detailRoute: StockDetailRoute = backStackEntry.toRoute()
                StockDetailScreen(
                    name = detailRoute.name,
                    symbol = detailRoute.symbol,
                    basePrice = detailRoute.price,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun AppBottomBar(navController: NavController, items: List<BottomNavItem<out Any>>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(item.route::class)
                } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
