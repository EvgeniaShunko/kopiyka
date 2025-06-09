package com.example.kopiyka.ui.saving

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopiyka.R
import kotlinx.coroutines.delay

@Composable
fun SavingInfoScreen(
    savingModel: String,
    budget: Int,
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE) }
    val username = prefs.getString("key_username", "") ?: ""

    val percentage = when (savingModel) {
        "Економна" -> 0.2
        "Ультимативна" -> 0.3
        else -> 0.1
    }
    val savedAmount = (budget * percentage).toInt()
    val adjustedBudget = budget - savedAmount

    LaunchedEffect(Unit) {
        prefs.edit()
            .putInt("key_budget_adjusted", adjustedBudget)
            .apply()
    }

    var progress by remember { mutableFloatStateOf(1f) }
    var showGreeting by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(5000)
        showGreeting = false
        delay(5000)
        onFinished()
    }

    LaunchedEffect(Unit) {
        repeat(100) {
            delay(100)
            progress -= 0.01f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.nav_bar_background)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = showGreeting,
            transitionSpec = {
                fadeIn(tween(500)) + slideInVertically { it } togetherWith
                        fadeOut(tween(500)) + slideOutVertically { -it }
            },
            label = "GreetingTransition"
        ) { isGreeting ->
            if (isGreeting) {
                Text(
                    text = "Привіт, $username!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text_primary),
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Ти обрав модель",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.text_primary),
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = savingModel,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(id = R.color.text_primary),
                        modifier = Modifier.padding(bottom = 12.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Давай заощадимо з твого бюджету",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.text_secondary),
                        modifier = Modifier.padding(bottom = 4.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "$savedAmount ₴ (${(percentage * 100).toInt()}%)",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { progress.coerceAtLeast(0f) },
            color = Color.White,
            trackColor = colorResource(id = R.color.text_secondary),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}
