package com.example.kopiyka.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kopiyka.ui.budget.BudgetDistributionScreen
import com.example.kopiyka.ui.onboarding.OnboardingScreen

@Composable
fun MainNavGraphWithOnboarding() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(
                onFinished = {
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("budget") {
            BudgetDistributionScreen(
                onStart = {
                    navController.navigate("main") {
                        popUpTo("budget") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainNavGraph()
        }
    }
}
