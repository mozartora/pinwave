package com.pinwave.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.pinwave.core.ui.components.BottomNavItem
import com.pinwave.core.ui.components.PulseBottomBar
import com.pinwave.ui.explore.ExploreScreen
import com.pinwave.ui.home.HomeScreen
import com.pinwave.ui.legal.LegalScreen
import com.pinwave.ui.onboarding.OnboardingScreen
import com.pinwave.ui.pin.PinDetailScreen
import com.pinwave.ui.pin.PinExplorerScreen
import com.pinwave.ui.paywall.PaywallScreen
import com.pinwave.ui.radar.RadarScreen
import com.pinwave.ui.saved.SavedScreen
import com.pinwave.ui.search.SearchScreen
import com.pinwave.ui.settings.SettingsScreen
import com.pinwave.ui.trend.TrendDetailScreen
import com.pinwave.ui.steal.StealTheTrendScreen
import com.pinwave.ui.content.TrendToContentScreen
import com.pinwave.ui.drop.TodaysDropScreen

private val tabItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Outlined.Home, "Home"),
    BottomNavItem(Routes.RADAR, "Radar", Icons.Outlined.Radar, "Trend radar"),
    BottomNavItem(Routes.EXPLORE, "Explore", Icons.Outlined.Search, "Explore trends"),
    BottomNavItem(Routes.SAVED, "Saved", Icons.Outlined.FavoriteBorder, "Saved trends"),
)

private val tabRoutes = tabItems.map { it.route }.toSet()

@Composable
fun PinwaveNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in tabRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PulseBottomBar(
                    items = tabItems,
                    currentRoute = currentRoute,
                    onSelect = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn() + slideInVertically { it / 24 } },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() + slideOutVertically { it / 24 } },
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onTrendClick = { navController.navigate(Routes.trend(it)) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) },
                    onSeeDrop = { navController.navigate(Routes.DROP) },
                    onCategoryClick = { navController.navigate(Routes.explore(it.name)) },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                )
            }
            composable(Routes.RADAR) {
                RadarScreen(onTrendClick = { navController.navigate(Routes.trend(it)) })
            }
            composable(
                route = Routes.EXPLORE_WITH_ARG,
                arguments = listOf(navArgument("category") { nullable = true; defaultValue = null }),
            ) {
                ExploreScreen(
                    onTrendClick = { navController.navigate(Routes.trend(it)) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) },
                )
            }
            composable(Routes.EXPLORE) {
                ExploreScreen(
                    onTrendClick = { navController.navigate(Routes.trend(it)) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) },
                )
            }
            composable(Routes.SAVED) {
                SavedScreen(
                    onTrendClick = { navController.navigate(Routes.trend(it)) },
                    onExploreClick = {
                        navController.navigate(Routes.EXPLORE) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onTrendClick = { navController.navigate(Routes.trend(it)) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.DROP) {
                TodaysDropScreen(
                    onTrendClick = { navController.navigate(Routes.trend(it)) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.TREND,
                arguments = listOf(navArgument("trendId") { type = NavType.StringType }),
            ) {
                TrendDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSeePins = { navController.navigate(Routes.pins(it)) },
                    onSteal = { navController.navigate(Routes.steal(it)) },
                    onCreateContent = { navController.navigate(Routes.content(it)) },
                )
            }
            composable(
                route = Routes.PINS,
                arguments = listOf(navArgument("trendId") { type = NavType.StringType }),
            ) {
                PinExplorerScreen(
                    onPinClick = { navController.navigate(Routes.pin(it)) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.PIN,
                arguments = listOf(navArgument("pinId") { type = NavType.StringType }),
            ) {
                PinDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.STEAL,
                arguments = listOf(navArgument("trendId") { type = NavType.StringType }),
            ) {
                StealTheTrendScreen(
                    onBack = { navController.popBackStack() },
                    onCreateContent = { navController.navigate(Routes.content(it)) },
                )
            }
            composable(
                route = Routes.CONTENT,
                arguments = listOf(navArgument("trendId") { type = NavType.StringType }),
            ) {
                TrendToContentScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onPaywall = { navController.navigate(Routes.PAYWALL) },
                    onLegal = { navController.navigate(Routes.legal(it)) },
                )
            }
            composable(Routes.PAYWALL) {
                PaywallScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.LEGAL,
                arguments = listOf(navArgument("page") { type = NavType.StringType }),
            ) {
                LegalScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
