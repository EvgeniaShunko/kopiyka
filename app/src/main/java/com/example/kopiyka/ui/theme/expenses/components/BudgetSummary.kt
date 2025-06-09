package com.example.kopiyka.ui.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BudgetSummary(
    total: Double,
    remaining: Double
) {
    val progress = if (total > 0) (remaining / total).toFloat() else 0f

    val progressColor = when {
        progress > 0.5f -> Color(0xFF4A90E2)
        progress > 0.25f -> Color(0xFFF5A623)
        progress > 0.10f -> Color(0xFFF86E6E)
        progress > 0f -> Color(0xFFD0021B)
        else -> Color.Gray
    }

    val currentMonth = remember {
        SimpleDateFormat("LLLL", Locale("uk"))
            .format(Date())
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1C1C1E),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = currentMonth,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Залишок: %.0f ₴ з %.0f ₴".format(remaining, total),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFFBBBBBB),
                fontSize = 14.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = progressColor,
            trackColor = Color(0xFF2C2C2E)
        )
    }
}


