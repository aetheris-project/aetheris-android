package com.aetheris.android.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aetheris.android.ui.screens.auth.LoginScreen
import com.aetheris.android.ui.screens.auth.ConnectScreen
import com.aetheris.android.ui.screens.dashboard.DashboardScreen
import com.aetheris.android.ui.screens.servers.ServerListScreen
import com.aetheris.android.ui.screens.servers.ServerDetailScreen
import com.aetheris.android.ui.screens.console.ConsoleScreen
import com.aetheris.android.ui.screens.billing.BillingScreen
import com.aetheris.android.ui.screens.billing.InvoiceDetailScreen
import com.aetheris.android.ui.screens.alerts.AlertsScreen
import com.aetheris.android.ui.screens.settings.SettingsScreen
import com.aetheris.android.ui.screens.install.InstallPanelScreen

sealed class Screen(val route: String) {
    data object Connect : Screen("connect")
    data object Login : Screen("login?serverUrl={serverUrl}") {
        fun createRoute(serverUrl: String = "") = "login?serverUrl=$serverUrl"
    }
    data object Dashboard : Screen("dashboard")
    data object Servers : Screen("servers")
    data object ServerDetail : Screen("servers/{serverId}") {
        fun createRoute(serverId: String) = "servers/$serverId"
    }
    data object Console : Screen("servers/{serverId}/console") {
        fun createRoute(serverId: String) = "servers/$serverId/console"
    }
    data object Billing : Screen("billing")
    data object InvoiceDetail : Screen("billing/invoices/{invoiceId}") {
        fun createRoute(invoiceId: String) = "billing/invoices/$invoiceId"
    }
    data object Alerts : Screen("alerts")
    data object Settings : Screen("settings")
    data object Install : Screen("install")
}

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Dashboard", "home"),
    BottomNavItem(Screen.Servers, "Servers", "dns"),
    BottomNavItem(Screen.Billing, "Billing", "receipt_long"),
    BottomNavItem(Screen.Alerts, "Alerts", "notifications"),
    BottomNavItem(Screen.Settings, "Settings", "settings"),
)

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: String
)

@Composable
fun AetherisNavGraph(
    navController: NavHostController,
    isAuthenticated: Boolean,
    initialServerUrl: String = ""
) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Screen.Dashboard.route else Screen.Connect.route,
        enterTransition = {
            fadeIn(animationSpec = tween(200)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(200)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(200)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(200)
            )
        }
    ) {
        composable(Screen.Connect.route) {
            ConnectScreen(
                onServerSelected = { url ->
                    navController.navigate(Screen.Login.createRoute(url)) {
                        popUpTo(Screen.Connect.route) { inclusive = true }
                    }
                },
                onConnectManually = { url ->
                    navController.navigate(Screen.Login.createRoute(url)) {
                        popUpTo(Screen.Connect.route) { inclusive = true }
                    }
                },
                onInstallClick = {
                    navController.navigate(Screen.Install.route)
                }
            )
        }

        composable(
            route = Screen.Login.route,
            arguments = listOf(
                navArgument("serverUrl") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val serverUrl = backStackEntry.arguments?.getString("serverUrl") ?: ""
            LoginScreen(
                serverUrl = serverUrl,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Servers.route) {
            ServerListScreen(
                onServerClick = { serverId ->
                    navController.navigate(Screen.ServerDetail.createRoute(serverId))
                }
            )
        }

        composable(
            route = Screen.ServerDetail.route,
            arguments = listOf(
                navArgument("serverId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            ServerDetailScreen(
                serverId = serverId,
                onConsoleClick = { navController.navigate(Screen.Console.createRoute(serverId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Console.route,
            arguments = listOf(
                navArgument("serverId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            ConsoleScreen(
                serverId = serverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Billing.route) {
            BillingScreen(
                onInvoiceClick = { invoiceId ->
                    navController.navigate(Screen.InvoiceDetail.createRoute(invoiceId))
                }
            )
        }

        composable(
            route = Screen.InvoiceDetail.route,
            arguments = listOf(
                navArgument("invoiceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
            InvoiceDetailScreen(
                invoiceId = invoiceId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Alerts.route) {
            AlertsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Screen.Connect.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Install.route) {
            InstallPanelScreen(
                onBack = { navController.popBackStack() },
                onComplete = { url ->
                    navController.navigate(Screen.Login.createRoute(url)) {
                        popUpTo(Screen.Install.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
