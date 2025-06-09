package com.example.kopiyka.ui.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun DailyBarChart(data: Map<Int, Float>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return

    val maxY = (data.values.maxOrNull() ?: 1f).coerceAtLeast(1f)

    val weekDays = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 4.dp)
    ) {
        val barWidth = size.width / 10f
        val spacing = size.width / 7f
        val chartHeight = size.height * 0.8f
        val bottom = size.height

        val lines = 5
        for (i in 1..lines) {
            val y = chartHeight * (i / lines.toFloat())
            drawLine(
                color = Color.LightGray.copy(alpha = 0.2f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        weekDays.forEachIndexed { index, dayOfWeek ->
            val label = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("uk"))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

            val value = data[dayOfWeek.value] ?: 0f
            val barHeight = (value / maxY) * chartHeight

            val x = spacing * index + spacing / 2 - barWidth / 2
            val y = bottom - barHeight

            drawRoundRect(
                color = Color(0xFF2196F3),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )

            drawContext.canvas.nativeCanvas.drawText(
                label,
                x + barWidth / 2,
                bottom + 24f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 28f
                    isAntiAlias = true
                }
            )
        }
    }
}
