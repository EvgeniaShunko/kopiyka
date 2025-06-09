package com.example.kopiyka.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.kopiyka.R
import com.example.kopiyka.data.local.OnboardingPreferencesDataSource
import com.example.kopiyka.data.repository.OnboardingRepositoryImpl
import com.example.kopiyka.domain.usecase.GetOnboardingDataUseCase
import com.example.kopiyka.ui.analytics.AnalyticsScreen
import com.example.kopiyka.ui.allexpenses.AllExpensesScreen
import com.example.kopiyka.ui.expenses.ExpensesScreen
import com.example.kopiyka.ui.savings.SavingsScreen
import com.example.kopiyka.ui.settings.SettingsScreen

sealed class BottomNavItem(val route: String, val iconActive: Int, val iconInactive: Int) {
    object Expenses    : BottomNavItem("expenses",     R.drawable.nav_expenses_active,     R.drawable.nav_expenses_inactive)
    object AllExpenses : BottomNavItem("all_expenses", R.drawable.nav_all_expenses_active, R.drawable.nav_all_expenses_inactive)
    object Analytics   : BottomNavItem("analytics",    R.drawable.nav_analytics_active,    R.drawable.nav_analytics_inactive)
    object Savings     : BottomNavItem("savings",      R.drawable.nav_savings_active,      R.drawable.nav_savings_inactive)
    object Settings    : BottomNavItem("settings",     R.drawable.nav_settings_active,     R.drawable.nav_settings_inactive)
}

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val onboardingDataSource = remember { OnboardingPreferencesDataSource(context) }
    val onboardingRepo = remember { OnboardingRepositoryImpl(onboardingDataSource) }
    val getOnboardingDataUseCase = remember { GetOnboardingDataUseCase(onboardingRepo) }

    val items = listOf(
        BottomNavItem.Expenses,
        BottomNavItem.AllExpenses,
        BottomNavItem.Analytics,
        BottomNavItem.Savings,
        BottomNavItem.Settings
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor   = MaterialTheme.colorScheme.onBackground,
        bottomBar      = { BottomBar(navController, items) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Expenses.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Expenses.route) {
                ExpensesScreen()
            }
            composable(BottomNavItem.AllExpenses.route) {
                AllExpensesScreen(getOnboardingDataUseCase = getOnboardingDataUseCase)
            }
            composable(BottomNavItem.Analytics.route) {
                AnalyticsScreen()
            }
            composable(BottomNavItem.Savings.route) {
                SavingsScreen()
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(getOnboardingDataUseCase = getOnboardingDataUseCase)
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController, items: List<BottomNavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val insets = WindowInsets.navigationBars.asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp + insets.calculateBottomPadding())
            .padding(bottom = insets.calculateBottomPadding())
            .background(colorResource(id = R.color.nav_bar_background))
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.DarkGray.copy(alpha = 0.5f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavBarIcon(
                    activeIcon = item.iconActive,
                    inactiveIcon = item.iconInactive,
                    isSelected = selected,
                    tint = if (selected) Color(0xFFE0E0E0) else Color(0xFF9E9E9E)
                ) {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

@Composable
private fun NavBarIcon(
    activeIcon: Int,
    inactiveIcon: Int,
    isSelected: Boolean,
    tint: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.8f else 1f)

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = if (isSelected) activeIcon else inactiveIcon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(32.dp)
        )
    }
}
