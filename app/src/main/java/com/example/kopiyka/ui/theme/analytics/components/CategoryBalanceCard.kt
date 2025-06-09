package com.example.kopiyka.ui.analytics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.kopiyka.R

@Composable
fun CategoryBalanceCard(
    category: String,
    remaining: Int,
    allocated: Int,
    modifier: Modifier = Modifier
) {
    val progress = (remaining.coerceAtLeast(0).toFloat() / allocated.coerceAtLeast(1).toFloat())

    val progressColor = when {
        progress > 0.5f -> Color(0xFF4A90E2)    // Синій
        progress > 0.25f -> Color(0xFFF5A623)   // Помаранчевий
        progress > 0.10f -> Color(0xFFF86E6E)   // Світло-червоний
        progress > 0f -> Color(0xFFD0021B)      // Яскраво-червоний
        else -> Color.Gray                      // Якщо зовсім немає
    }

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "CategoryCardScale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.card_background))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .padding(16.dp)
    ) {
        Text(category, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Залишок: ${remaining} ₴ із ${allocated} ₴", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = progressColor
        )
    }
}
