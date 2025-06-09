package com.example.kopiyka

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.example.kopiyka.navigation.MainNavGraph
import com.example.kopiyka.navigation.MainNavGraphWithOnboarding
import com.example.kopiyka.ui.theme.KopiykaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔧 Оформлення кольору навбару
        val navColor = ContextCompat.getColor(this, R.color.nav_bar_background)
        window.navigationBarColor = navColor

        setContent {
            KopiykaTheme {
                // ✅ Визначаємо, чи показувати онбординг
                val prefs = getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
                val isOnboardingCompleted = prefs.getBoolean("onboarding_completed", false)

                if (isOnboardingCompleted) {
                    MainNavGraph()
                } else {
                    MainNavGraphWithOnboarding()
                }
            }
        }
    }
}
